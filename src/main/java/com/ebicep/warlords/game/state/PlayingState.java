package com.ebicep.warlords.game.state;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.commands.debugcommands.misc.RecordGamesCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.player.CustomScoreboard;
import com.ebicep.warlords.player.ExperienceManager;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.bukkit.RemoveEntities;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class PlayingState implements State, TimerDebugAble {

    private final Game game;
    private WarlordsGameTriggerWinEvent winEvent;
    private int counter = 0;
    private int timer = 0;

    public PlayingState(@Nonnull Game game) {
        this.game = game;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    @Override
    @SuppressWarnings("null")
    public void begin() {
        this.game.setAcceptsSpectators(true);
        this.game.setAcceptsPlayers(false);
        this.resetTimer();
        RemoveEntities.doRemove(this.game);
        for (Option option : game.getOptions()) {
            option.start(game);
        }

        this.game.forEachOfflinePlayer((player, team) -> {
            if (team != null) {
                PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                Warlords.addPlayer(new WarlordsPlayer(
                        player,
                        this,
                        team,
                        playerSettings
                ));
            }
        });
        this.game.forEachOfflineWarlordsPlayer(wp -> {
            CustomScoreboard customScoreboard = Warlords.playerScoreboards.get(wp.getUuid());
            updateBasedOnGameState(customScoreboard, wp);
            if (wp.getEntity() instanceof Player) {
                wp.applySkillBoost((Player) wp.getEntity());
            }
        });

        if (DatabaseManager.playerService != null) {
            Warlords.newChain().async(() -> game.forEachOfflinePlayer((player, team) -> {
                DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                DatabaseManager.updatePlayerAsync(databasePlayer);
                DatabaseManager.loadPlayer(player.getUniqueId(), PlayersCollections.SEASON_6, () -> {
                });
                DatabaseManager.loadPlayer(player.getUniqueId(), PlayersCollections.WEEKLY, () -> {
                });
                DatabaseManager.loadPlayer(player.getUniqueId(), PlayersCollections.DAILY, () -> {
                });
            })).execute();
        } else {
            System.out.println("ATTENTION - playerService is null");
        }
        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                game.setNextState(new EndState(game, event));
                winEvent = event;
            }
        });
        GameRunnable.create(game, this::updateScoreboard).runTaskTimer(0, 10);
        new GameRunnable(game) {
            @Override
            public void run() {
                counter++;
                timer += GameRunnable.SECOND;
                if (counter >= 60) {
                    counter -= 60;
                    PlayerFilter.playingGame(game).forEach(wp -> wp.getMinuteStats().advanceMinute());
                }
                PlayerFilter.playingGame(game).forEach(wp -> wp.getSecondStats().advanceSecond());
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
        game.registerGameMarker(TimerSkipAbleMarker.class, (delay) -> {
            counter += delay / GameRunnable.SECOND;
            timer += delay;
        });

        Warlords.getInstance().hideAndUnhidePeople();
    }

    @Override
    public State run() {
        game.players().forEach(uuidTeamEntry -> {
            WarlordsPlayer wp = Warlords.getPlayer(uuidTeamEntry.getKey());
            ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
            if (wp != null) {
                byteArrayDataOutput.writeUTF(wp.getName());
                byteArrayDataOutput.writeInt((int) wp.getEnergy());
                byteArrayDataOutput.writeInt((int) wp.getMaxEnergy());
                AbstractPlayerClass spec = wp.getSpec();
                byteArrayDataOutput.writeInt(spec.getRed().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getRed().getCurrentCooldown() + .5));
                byteArrayDataOutput.writeInt(spec.getPurple().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getPurple().getCurrentCooldown() + .5));
                byteArrayDataOutput.writeInt(spec.getBlue().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getBlue().getCurrentCooldown() + .5));
                byteArrayDataOutput.writeInt(spec.getOrange().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getOrange().getCurrentCooldown() + .5));
                game.spectators().forEach(uuid -> {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.getName().equals("sumSmash")) {
                        player.sendPluginMessage(Warlords.getInstance(), "Warlords", byteArrayDataOutput.toByteArray());
                    }
                });
            }
        });

        return null;
    }

    @Override
    @SuppressWarnings("null")
    public void end() {

        System.out.println(" ----- GAME END ----- ");
        System.out.println("RecordGames = " + RecordGamesCommand.recordGames);
        System.out.println("Force End = " + (winEvent == null));
        System.out.println("Player Count = " + game.playersCount());
        System.out.println("Players = " + game.getPlayers().keySet());
        System.out.println("Timer = " + timer);
        System.out.println("Private = " + game.getAddons().contains(GameAddon.PRIVATE_GAME));
        System.out.println("GameMode = " + game.getGameMode());
        System.out.println("Map = " + game.getMap());
        System.out.println("Game Addons = " + game.getAddons());
        System.out.println(" ----- GAME END ----- ");

        List<WarlordsPlayer> players = PlayerFilter.playingGame(game).toList();
        if (players.isEmpty()) {
            return;
        }
        //PUBS
        if (!game.getAddons().contains(GameAddon.PRIVATE_GAME) && !game.getAddons().contains(GameAddon.IMPOSTER_MODE) && winEvent != null && game.playersCount() >= 12) {
            String gameEnd = "[GAME] A Public game ended with ";
            // TODO parse winEvent better here
            if (winEvent != null && winEvent.getDeclaredWinner() == Team.BLUE) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**BLUE** winning " + game.getPoints(Team.BLUE) + " to " + game.getPoints(Team.RED), false, true);
            } else if (winEvent != null && winEvent.getDeclaredWinner() == Team.RED) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**RED** winning " + game.getPoints(Team.RED) + " to " + game.getPoints(Team.BLUE), false, true);
            } else {
                BotManager.sendMessageToNotificationChannel(gameEnd + "a **DRAW**", false, true);
            }

            DatabaseGameBase.addGame(game, winEvent, true);

            if (DatabaseManager.playerService == null) return;
            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5))
                    .syncLast(databasePlayers -> {
                        SRCalculator.databasePlayerCache = databasePlayers;
                        SRCalculator.recalculateSR();
                    })
                    .execute();
        } //COMPS
        else if (!game.getAddons().contains(GameAddon.IMPOSTER_MODE) && winEvent != null && game.playersCount() >= 16 && timer >= 6000) {
            String gameEnd = "[GAME] A game ended with ";
            if (winEvent != null && winEvent.getDeclaredWinner() == Team.BLUE) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**BLUE** winning " + game.getPoints(Team.BLUE) + " to " + game.getPoints(Team.RED), true, false);
            } else if (winEvent != null && winEvent.getDeclaredWinner() == Team.RED) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**RED** winning " + game.getPoints(Team.RED) + " to " + game.getPoints(Team.BLUE), true, false);
            } else {
                BotManager.sendMessageToNotificationChannel(gameEnd + "a **DRAW**", true, false);
            }

            DatabaseGameBase.addGame(game, winEvent, RecordGamesCommand.recordGames);
        } //END GAME
        else {
            if (game.getAddons().contains(GameAddon.PRIVATE_GAME) && game.playersCount() >= 6 && timer >= 6000) {
                DatabaseGameBase.addGame(game, winEvent, false);
            } else {
                System.out.println(ChatColor.GREEN + "[Warlords] This PUB/COMP game was not added to the database and player information remained the same");
            }
        }
    }

    @Override
    public void skipTimer() {
        // TODO loop over options and decrement them is needed
        int maxSkip = Integer.MAX_VALUE;
        for (TimerSkipAbleMarker marker : game.getMarkers(TimerSkipAbleMarker.class)) {
            if (marker.getDelay() > 0) {
                maxSkip = Math.min(marker.getDelay(), maxSkip);
            }
        }
        for (TimerSkipAbleMarker marker : game.getMarkers(TimerSkipAbleMarker.class)) {
            marker.skipTimer(maxSkip);
        }
    }

    @Override
    public void resetTimer() throws IllegalStateException {
    }

    private void updateScoreboard() {
        game.forEachOnlinePlayer((player, team) -> {
            updateBasedOnGameState(Warlords.playerScoreboards.get(player.getUniqueId()), Warlords.getPlayer(player));
        });
    }

    private void updateHealth(@Nonnull CustomScoreboard customScoreboard) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        Objective health = customScoreboard.getHealth();
        if (health == null || scoreboard.getObjective("health") == null) {
            health = scoreboard.registerNewObjective("health", "dummy");
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            health.setDisplayName(ChatColor.RED + "❤");
            customScoreboard.setHealth(health);
        }
        Objective finalHealth = health;
        this.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            if (warlordsPlayer != null) {
                finalHealth.getScore(warlordsPlayer.getName()).setScore(warlordsPlayer.getHealth());
            }
        });
    }

    private void updateNames(@Nonnull CustomScoreboard customScoreboard) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        this.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            if (warlordsPlayer != null) {
                if (scoreboard.getTeam(warlordsPlayer.getName()) == null) {
                    org.bukkit.scoreboard.Team temp = scoreboard.registerNewTeam(warlordsPlayer.getName());
                    temp.setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] " + team.teamColor());
                    temp.addEntry(warlordsPlayer.getName());
                    temp.setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GOLD + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass())) + ChatColor.DARK_GRAY + "]");
                } else {
                    scoreboard.getTeam(warlordsPlayer.getName()).setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] " + team.teamColor());
                    if (warlordsPlayer.getCarriedFlag() != null) {
                        scoreboard.getTeam(warlordsPlayer.getName()).setSuffix(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass())) + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + "⚑");
                    } else {
                        scoreboard.getTeam(warlordsPlayer.getName()).setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass())) + ChatColor.DARK_GRAY + "]");
                    }
                }
            }
        });
    }

    /**
     * Updates the names of the player on the scoreboard. To be used when the spec of a warlord player changes
     * @param warlordsPlayer the player changing
     */
    public void updatePlayerName(@Nonnull WarlordsPlayer warlordsPlayer) {
        this.getGame().forEachOfflinePlayer((player, team) -> {
            Scoreboard scoreboard = Warlords.playerScoreboards.get(player.getUniqueId()).getScoreboard();
            int level = ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass());
            //System.out.println("Updating scorebopard for " + player + " setting " + warlordsPlayer + " to team " + warlordsPlayer.getTeam());
            scoreboard.getTeam(warlordsPlayer.getName()).setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] " + warlordsPlayer.getTeam().teamColor());
            scoreboard.getTeam(warlordsPlayer.getName()).setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" + (level < 10 ? "0" : "") + level + ChatColor.DARK_GRAY + "]");
        });
    }

    private void updateBasedOnGameScoreboards(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer) {
        List<String> scoreboard = new ArrayList<>();

        String lastGroup = null;
        boolean lastWasEmpty = true;
        for (ScoreboardHandler handler : Utils.iterable(game
                .getScoreboardHandlers()
                .stream()
                .sorted(Comparator.comparing((ScoreboardHandler sh) -> sh.getPriority(warlordsPlayer)))
        )) {
            String group = handler.getGroup();
            if ((lastGroup == null || !lastGroup.equals(group)) && !lastWasEmpty) {
                scoreboard.add("");
                lastWasEmpty = true;
            }
            lastGroup = group;
            List<String> handlerContents = handler.computeLines(warlordsPlayer);
            if (!handlerContents.isEmpty()) {
                lastWasEmpty = false;
                scoreboard.addAll(handlerContents);
            }
        }
        customScoreboard.giveNewSideBar(false, scoreboard);
    }

    private void updateBasedOnGameState(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer) {
        this.updateHealth(customScoreboard);
        this.updateNames(customScoreboard);
        this.updateBasedOnGameScoreboards(customScoreboard, warlordsPlayer);
    }

    @Override
    public void onPlayerReJoinGame(@Nonnull Player player) {
        WarlordsPlayer wp = Warlords.getPlayer(player);
        if (wp == null) {
            // Spectator
            player.setGameMode(GameMode.SPECTATOR);
            Location spawn = Stream.concat(
                    getGame().getMarkers(SpawnLocationMarker.class).stream(),
                    getGame().getMarkers(LobbyLocationMarker.class).stream()
            ).map(LocationMarker::getLocation).collect(Utils.randomElement());
            player.teleport(spawn);
        }
        CustomScoreboard sb = Warlords.playerScoreboards.get(player.getUniqueId());
        updateBasedOnGameState(sb, wp);
    }

    public int getTicksElapsed() {
        return this.timer;
    }

}
