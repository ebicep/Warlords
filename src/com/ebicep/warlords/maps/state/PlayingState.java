package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.PlayerSettings;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.events.WarlordsPointsChangedEvent;
import com.ebicep.warlords.maps.flags.FlagManager;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Gates;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.RemoveEntities;
import static com.ebicep.warlords.util.Utils.sendMessage;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PlayingState implements State, TimerDebugAble {
    private static final int GATE_TIMER = 10 * 20;
    private static final int POWERUP_TIMER = 30 * 20;
    private static final int OVERTIME_TIME = 30 * 20;

    private static final int SCORE_KILL_POINTS = 5;
    private static final int SCORE_CAPTURE_POINTS = 250;
    
    private static final int ENDING_SCORE_LIMIT = 1000;
    
    private int timer = 0;
    private int gateTimer = 0;
    private int powerupTimer = 0;
    private boolean overTimeActive = false;
    private int pointLimit = 0;
    private final Game game;
    private boolean forceEnd;
    
    private final EnumMap<Team, Stats> stats = new EnumMap(Team.class);
    {
        resetStats();
    }
    

    @Nullable
    private FlagManager flags = null;
    @Nullable
    private BukkitTask powerUps = null;

    public PlayingState(@Nonnull Game game) {
        this.game = game;
    }

    public void addKill(@Nonnull Team victim, boolean isSuicide) {
        Stats myStats = getStats(victim);
        myStats.deaths++;
        Stats enemyStats = getStats(victim.enemy());
        enemyStats.kills++;
        addPoints(victim.enemy(), SCORE_KILL_POINTS);
        switch(victim) { // TODO We already track this in stats, is this still needed?
            case RED:
                Warlords.blueKills++;
                break;
            case BLUE:
                Warlords.redKills++;
                break;
        }
    }
    
    @Nonnull
    public Stats getStats(@Nonnull Team team) {
        return stats.get(team);
    }

    public void addPoints(@Nonnull Team team, int i) {
        getStats(team).addPoints(i);
    }
    
    @Deprecated
    public int getBluePoints() {
        return stats.get(Team.BLUE).points();
    }

    @Deprecated
    public void addBluePoints(int i) {
        this.addPoints(Team.BLUE, i);
    }
    
    @Deprecated
    public int getRedPoints() {
        return stats.get(Team.RED).points();
    }

    @Deprecated
    public void addRedPoints(int i) {
        this.addPoints(Team.RED, i);
    }

    public int getTimer() {
        return timer;
    }

    public int getTimerInSeconds() {
        return getTimer() / 20;
    }

    public boolean isOvertime() {
        return this.overTimeActive;
    }

    @Nonnull
    @SuppressWarnings("null")
    public FlagManager flags() {
        if (this.flags == null) {
            throw new IllegalStateException("Cannot acces flag sub component, state not enabled");
        }
        return this.flags;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    @Override
    @SuppressWarnings("null")
    public void begin() {
        this.resetTimer();
        this.forceEnd = false;
        this.gateTimer = GATE_TIMER;
        this.powerupTimer = POWERUP_TIMER;
        RemoveEntities.doRemove(this.game.getMap());
        this.flags = new FlagManager(this, game.getMap().getRedFlag(), game.getMap().getBlueFlag());
        Set<UUID> wantedTeamBlue = new HashSet<>();
        Set<UUID> wantedTeamRed = new HashSet<>();
        Set<UUID> wantedNoTeam = new HashSet<>();
        
        // Collect players by their preferences
        Map<Team, List<OfflinePlayer>> preferedTeams = this.game.offlinePlayers().collect(
            Collectors.groupingBy(entry -> {
                OfflinePlayer p = entry.getKey();
                Team team = p.getPlayer() != null ? Team.getSelected((Player) p.getPlayer()) : null;
                return team;
            },
            Collectors.mapping(
                Map.Entry::getKey,
                Collectors.toList()
            )
        ));
        List<OfflinePlayer> wantedRed = preferedTeams.get(Team.RED);
        List<OfflinePlayer> wantedBlue = preferedTeams.get(Team.BLUE);
        for (OfflinePlayer p : preferedTeams.get(null)) {
            Bukkit.broadcastMessage(p.getName() + " did not choose a team!");
            if (wantedRed.size() < wantedBlue.size()) {
                wantedRed.add(p);
            } else {
                wantedBlue.add(p);
            }
        }
        
        for (Map.Entry<Team, List<OfflinePlayer>> list : preferedTeams.entrySet()) {
            if (list.getKey() == null) {
                continue;
            }
            for (OfflinePlayer peep : list.getValue()) {
                this.game.setPlayerTeam(peep, list.getKey());
            }
        }
        
        this.game.forEachOfflinePlayer((player, team) -> {
            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
            Warlords.addPlayer(new WarlordsPlayer(
                player,
                this,
                team,
                false,
                playerSettings
            ));
        });
        this.game.forEachOfflinePlayer((p, team) -> {
            CustomScoreboard scoreboard = Warlords.getPlayer(p).getScoreboard();
            scoreboard.updateHealth();
            scoreboard.updateBasedOnGameState(this);
            scoreboard.updateKillsAssists();
            scoreboard.updateNames();
        });
        this.game.forEachOnlinePlayer((player, team) -> {
            Warlords.getPlayer(player).updatePlayerReference(player);
            PacketUtils.sendTitle(player, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);
        });
    }

    @Override
    public State run() {
        this.timer--;
        if (forceEnd) {
            return getEndState(null);
        }
        if (this.timer <= 0) {
            if (this.overTimeActive) {
                return getEndState(null);
            } else {
                State next = nextStateByPoints();
                if(next == null) {
                    this.timer = OVERTIME_TIME;
                    this.overTimeActive = true;
                    assert getStats(Team.BLUE).points == getStats(Team.RED).points;
                    this.pointLimit = getStats(Team.BLUE).points + 25;
                    this.game.forEachOnlinePlayer((player, team) -> {
                        player.sendMessage("Overtime is now active!");
                    });
                } else {
                    return next;
                }
            }
        }
        for (WarlordsPlayer value : Warlords.getPlayers().values()) {
            value.getScoreboard().updateBasedOnGameState(this);
        }
        if (getStats(Team.BLUE).points >= this.pointLimit || getStats(Team.RED).points >= this.pointLimit) {
            return nextStateByPoints();
        }
        if (gateTimer >= 0) {
            gateTimer--;
            if (gateTimer % 20 == 0) {
                int remaining = gateTimer / 20;
                game.forEachOnlinePlayer((player, team) -> {
                    player.playSound(player.getLocation(), remaining == 0 ? Sound.WITHER_SPAWN : Sound.NOTE_STICKS, 1, 1);
                    String number;
                    if (remaining >= 8) {
                        number = ChatColor.GREEN.toString();
                    } else if (remaining >= 4) {
                        number = ChatColor.YELLOW.toString();
                    } else {
                        number = ChatColor.RED.toString();
                    }
                    number += remaining;
                    PacketUtils.sendTitle(player, number, "", 0, 40, 0);
                });
                switch(remaining) {
                    case 0:
                        Gates.changeGates(game.getMap(), true);
                        game.forEachOnlinePlayer((player, team) -> {
                            sendMessage(player, false, ChatColor.YELLOW + "Gates opened! " + ChatColor.RED + "FIGHT!");
                            PacketUtils.sendTitle(player, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);
                        });
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 10:
                        game.forEachOnlinePlayer((player, team) -> {
                            String s = remaining == 1 ? "" : "s";
                            sendMessage(player, false, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + "10" + ChatColor.YELLOW + " second" + s + "!");
                        });
                        break;
                }
            }
        }
        if (powerupTimer >= 0) {
            powerupTimer--;
            if (powerupTimer == 0) {
                if (this.powerUps != null) {
                    this.powerUps.cancel();
                }
                this.powerUps = new PowerupManager(game.getMap()).runTaskTimer(Warlords.getInstance(), 0, 0);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("null")
    public void end() {
        if(this.flags != null) {
            this.flags.stop();
            this.flags = null;
        }
        if(this.powerUps != null) {
            this.powerUps.cancel();
            this.powerUps = null;
        }
    }

    @Override
    public void skipTimer() {
        if(this.gateTimer > 0) {
            this.timer -= this.gateTimer - 1;
            this.gateTimer = 1;
        } else {
            this.timer = 0;
        }
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = game.getMap().getGameTimerInTicks();
        this.pointLimit = ENDING_SCORE_LIMIT;
        this.overTimeActive = false;
    }
    
    private EndState getEndState(@Nullable Team winner) {
        return new EndState(this.game, winner, this.getStats(Team.RED), this.getStats(Team.BLUE));
    }
    
    @Nullable
    public Team calculateWinnerByPoints() {
        int redPoints = getStats(Team.RED).points();
        int bluePoints = getStats(Team.BLUE).points();
        if(redPoints > bluePoints) {
            return Team.RED;
        }
        if(bluePoints > redPoints) {
            return Team.BLUE;
        }
        return null;
    }
    
    @Nullable
    private State nextStateByPoints() {
        Team winner = calculateWinnerByPoints();
        if (winner != null) {
            return getEndState(winner);
        }
        return null;
    }
    
    public void resetStats() {
        for(Team team : Team.values()) {
            stats.put(team, new Stats(team));
        }
    }

    public void addCapture(WarlordsPlayer capper) {
        getStats(capper.getTeam()).captures++;
        addPoints(capper.getTeam(), SCORE_CAPTURE_POINTS);
    }

    public void endGame() {
        this.forceEnd = true;
    }
    
    public class Stats {
        private final Team team;
        int points;
        int kills;
        int captures;
        int deaths;

        public Stats(Team team) {
            this.team = team;
        }

        public int points() {
            return points;
        }

        public void setPoints(int points) {
            int oldPoints = this.points;
            this.points = points;
            Bukkit.getPluginManager().callEvent(new WarlordsPointsChangedEvent(game, team, oldPoints, this.points));
        }

        private void addPoints(int i) {
            setPoints(points() + i);
        }

        public int kills() {
            return kills;
        }

        public void setKills(int kills) {
            this.kills = kills;
        }

        public int captures() {
            return captures;
        }

        public void setCaptures(int captures) {
            this.captures = captures;
        }

        public int deaths() {
            return deaths;
        }

        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }

        @Override
        public String toString() {
            return "Stats{" + "points=" + points + ", kills=" + kills + ", captures=" + captures + ", deaths=" + deaths + '}';
        }
        
    }
}
