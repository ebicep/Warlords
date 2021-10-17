package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Gates;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.CustomScoreboard;
import com.ebicep.warlords.player.CustomScoreboardPair;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.Utils.sendMessage;

public class PreLobbyState implements State, TimerDebugAble {

    private int timer = 0;
    private final Game game;
    private final Map<UUID, TeamPreference> teamPreferences = new HashMap<>();

    public PreLobbyState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        timer = game.getMap().getCountdownTimerInTicks();
        Gates.changeGates(game.getMap(), false);
        game.forEachOnlinePlayer((player, team) -> {
            giveLobbyScoreboard(true, player);
        });
    }

    @Override
    public State run() {
        int players = game.playersCount();
        if (players >= game.getMap().getMinPlayers()) {
            if (timer % 20 == 0) {
                int time = timer / 20;
                game.forEachOnlinePlayer((player, team) -> {
                    giveLobbyScoreboard(false, player);
                    player.setAllowFlight(false);
                });
                if (time == 30) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + "30 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 20) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in 20 seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 10) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + "10 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time <= 5 && time != 0) {
                    game.forEachOnlinePlayer((player, team) -> {
                        String s = time == 1 ? "!" : "s!";
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " second" + s);
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 0) {
                    game.forEachOnlinePlayer((player, team) -> {
                        Utils.sendMessage(player, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        Utils.sendMessage(player, true, "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords");
                        Utils.sendMessage(player, true, "");
                        Utils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "Steal and capture the enemy team's flag to");
                        Utils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "earn " + ChatColor.AQUA + ChatColor.BOLD + "250 " + ChatColor.YELLOW + ChatColor.BOLD + "points! The first team with a");
                        Utils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "score of " + ChatColor.AQUA + ChatColor.BOLD + "1000 " + ChatColor.YELLOW + ChatColor.BOLD + "wins!");
                        Utils.sendMessage(player, true, "");
                        Utils.sendMessage(player, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        player.playSound(player.getLocation(), "gamestart", 1, 1);
                        player.setAllowFlight(false);
                    });
                }
            }

            if (timer <= 0) {
                return new PlayingState(game);
            }
            timer--;
        } else {
            timer = game.getMap().getCountdownTimerInTicks();
        }
        return null;
    }

    @Override
    public void end() {
        updateTeamPreferences();
        distributePeopleOverTeams();
    }

    public void giveLobbyScoreboard(boolean init, Player player) {
        CustomScoreboard customScoreboard = Warlords.playerScoreboards.get(player.getUniqueId());

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        int time = timer / 20;

        String mapPrefix = ChatColor.WHITE + "Map: " + ChatColor.GREEN;
        String mapSuffix;
        if(game.getMap().getMapName().length() >= 16) {
            mapPrefix += game.getMap().getMapName().substring(0, 7);
            mapSuffix = game.getMap().getMapName().substring(7);
        } else {
            mapSuffix = game.getMap().getMapName();
        }

        customScoreboard.giveNewSideBar(init,
                new CustomScoreboardPair(ChatColor.GRAY + dateString, ""),
                new CustomScoreboardPair("  ", ""),
                new CustomScoreboardPair(mapPrefix, mapSuffix),
                new CustomScoreboardPair(ChatColor.WHITE + "Players: ", ChatColor.GREEN.toString() + game.playersCount() + "/" + game.getMap().getMaxPlayers()),
                new CustomScoreboardPair("   ", ""),
                new CustomScoreboardPair(ChatColor.WHITE + "Starting in: ", ChatColor.GREEN + (time < 10 ? "00:0" : "00:") + time + ChatColor.WHITE + " to"),
                new CustomScoreboardPair(ChatColor.WHITE + "allow time ", "for "),
                new CustomScoreboardPair(ChatColor.WHITE + "additional ", "players"),
                new CustomScoreboardPair("    ", ""),
                new CustomScoreboardPair(ChatColor.GOLD + "Lv90 ", Classes.getClassesGroup(Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass()).name),
                new CustomScoreboardPair(ChatColor.WHITE + "Spec: ", ChatColor.GREEN + Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass().name),
                new CustomScoreboardPair("     ", ""),
                new CustomScoreboardPair(ChatColor.YELLOW + Warlords.VERSION, "")
        );


    }

    private void updateTeamPreferences() {
        this.game.offlinePlayers().forEach((e) -> {
            Team selectedTeam = Warlords.getPlayerSettings(e.getKey().getUniqueId()).getWantedTeam();
            if (selectedTeam == null) {
                Bukkit.broadcastMessage(ChatColor.GOLD + e.getKey().getName() + " §7did not choose a team!");
            }
            TeamPreference newPref = new TeamPreference(
                    e.getValue(),
                    selectedTeam == null ? e.getValue() : selectedTeam,
                    selectedTeam == null ? TeamPriority.NO_PREFERENCE : TeamPriority.PLAYER_PREFERENCE
            );
            teamPreferences.compute(e.getKey().getUniqueId(), (k, oldPref) ->
                    oldPref == null || oldPref.priority() < newPref.priority() ? newPref : oldPref
            );
        });
    }

    private boolean tryMovePeep(Map.Entry<UUID, TeamPreference> entry, Team target) {
        boolean canSwitchPeepTeam;
        if (entry.getValue().wantedTeam != target) {
            switch (entry.getValue().priority) {
                case FORCED_PREFERENCE:
                    canSwitchPeepTeam = false;
                    break;
                case PLAYER_PREFERENCE:
                    canSwitchPeepTeam = false; // Always enforce teams people manually have picked, probably set to true in the future
                    break;
                default:
                    canSwitchPeepTeam = true;
            }
        } else {
            canSwitchPeepTeam = true;
        }

        if (canSwitchPeepTeam) {
            this.game.setPlayerTeam(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue().wantedTeam);
            return true;
        }
        return false;
    }

    private void distributePeopleOverTeams() {
        List<Map.Entry<UUID, TeamPreference>> prefs = teamPreferences.entrySet().stream().collect(Collectors.toList());

        if (!prefs.isEmpty()) {
            int redIndex = 0;
            int blueIndex = prefs.size() - 1;

            boolean canPickRed = true;
            boolean canPickBlue = true;

            do {
                if (redIndex == blueIndex && canPickBlue && canPickRed) {
                    //We have 1 person remaining, and both teams still have room, put the player at its wanted team
                    tryMovePeep(prefs.get(redIndex), prefs.get(redIndex).getValue().wantedTeam);
                    canPickBlue = false;
                    canPickRed = false;
                }
                if (canPickRed) {
                    if (tryMovePeep(prefs.get(redIndex), Team.RED)) {
                        redIndex++;
                        canPickRed = redIndex < prefs.size();
                    } else {
                        canPickRed = false;
                    }
                }
                if (canPickBlue && redIndex <= blueIndex) {
                    if (tryMovePeep(prefs.get(blueIndex), Team.BLUE)) {
                        blueIndex--;
                        canPickBlue = blueIndex >= 0;
                    } else {
                        canPickBlue = false;
                    }
                }
            } while ((canPickRed || canPickBlue) && redIndex <= blueIndex);
        }
    }

    @Override
    public void skipTimer() {
        this.timer = 0;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = game.getMap().getCountdownTimerInTicks();
    }

    private final class TeamPreference implements Comparable<TeamPreference> {
        final Team wantedTeam;
        final TeamPriority priority;
        final Team currentTeam;

        public TeamPreference(Team currentTeam, Team wantedTeam, TeamPriority priority) {
            this.currentTeam = currentTeam;
            this.wantedTeam = wantedTeam;
            this.priority = priority;
        }

        public int priority() {
            return this.priority.ordinal() * 2 + 1 + (wantedTeam == currentTeam ? 1 : 0);
        }

        // Team red is negative, blue is positive
        public int toInt() {
            return (wantedTeam == Team.RED ? -1 : 1) * priority();
        }

        @Override
        public int compareTo(TeamPreference o) {
            return Integer.compare(toInt(), o.toInt());
        }

    }

    private enum TeamPriority {
        FORCED_PREFERENCE,
        PLAYER_PREFERENCE,
        NO_PREFERENCE,
    }
}
