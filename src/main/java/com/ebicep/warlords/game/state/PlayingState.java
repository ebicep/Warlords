package com.ebicep.warlords.game.state;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.RecordGamesCommand;
import com.ebicep.warlords.commands.miscellaneouscommands.StreamChaptersCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.*;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.PlayerStatisticsSecond;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class PlayingState implements State, TimerDebugAble {

    private final Game game;
    private WarlordsGameTriggerWinEvent winEvent;
    private int counter = 0;
    private int timer = 0;
    private PlayingStateScoreboardUpdater updater;
    private AtomicBoolean gameAdded = new AtomicBoolean(false);

    public PlayingState(@Nonnull Game game) {
        this.game = game;
        this.updater = new PlayingStateScoreboardUpdater(game);
    }

    @Override
    @SuppressWarnings("null")
    public void begin() {
        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game " + game.getGameId() + " has started");
//        Warlords.getGameManager().getGames().stream()
//                .filter(gameHolder -> gameHolder.getGame() != null && gameHolder.getGame().equals(game))
//                .findAny()
//                .ifPresent(gameHolder -> {
//                    ChatChannels.sendDebugMessage((CommandIssuer) null,
//                            Component.text("Started Game: " + game.getGameMode() + " - " + gameHolder.getName(), NamedTextColor.LIGHT_PURPLE)
//                    );
//                });
        this.game.setAcceptsSpectators(true);
        this.game.setAcceptsPlayers(false);
        this.resetTimer();
        EntitiesUtils.doRemove(this.game);
        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Adding game options");

        List<Map.Entry<OfflinePlayer, Team>> players = game.offlinePlayersWithoutSpectators().toList();
        players.forEach(entry -> {
            OfflinePlayer player = entry.getKey();
            Team team = entry.getValue();
            Player p = player.getPlayer();
            if (team == null || (!player.isOnline() && com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode()))) {
                return;
            }
            DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player.getUniqueId());
            Specializations selectedSpec = databasePlayer.getLastSpec();
            if (selectedSpec.isBanned()) {
                for (Specializations value : Specializations.VALUES) {
                    if (value.isBanned()) {
                        continue;
                    }
                    if (p != null) {
                        p.sendMessage(Component.text(selectedSpec.name + " is currently disabled. Your specialization has been changed.", NamedTextColor.RED));
                    }
                    databasePlayer.setLastSpec(value);
                    break;
                }
                if (databasePlayer.getLastSpec().isBanned()) {
                    if (p != null) {
                        game.forEachOnlinePlayer((player1, team1) -> {
                            player1.sendMessage(Component.text("All specializations are currently disabled. Game closing.", NamedTextColor.RED));
                        });
                        for (GameManager.GameHolder gameHolder : Warlords.getGameManager().getGames()) {
                            if (gameHolder.getGame() == game) {
                                gameHolder.forceEndGame();
                            }
                        }
                    }
                }
            }
        });

        game.forEachEnabledOption(option -> option.start(game));
        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game options added");

        List<WarlordsEntity> warlordsEntities = new ArrayList<>();
        players.forEach(entry -> {
            OfflinePlayer player = entry.getKey();
            Team team = entry.getValue();
            Player p = player.getPlayer();
            if (team == null || (!player.isOnline() && com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode()))) {
                return;
            }
            WarlordsPlayer warlordsEntity = new WarlordsPlayer(
                    player,
                    this.getGame(),
                    team
            );
            Warlords.addPlayer(warlordsEntity);
            warlordsEntities.add(warlordsEntity);
            if (p != null) {
                p.getInventory().setHeldItemSlot(0);
            }
            Utils.resetPlayerMovementStatistics(player);
        });
        game.forEachEnabledOption(option -> option.afterAllWarlordsEntitiesCreated(warlordsEntities));

        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                game.setNextState(new EndState(game, event, gameAdded));
                winEvent = event;
            }
        });
        new GameRunnable(game) {

            @Override
            public void run() {
                updater.update();
            }
        }.runTaskTimer(0, 1);

        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Started recording timed stats");

        new GameRunnable(game) {

            @Override
            public void run() {
                counter++;
                timer += GameRunnable.SECOND;
                if (counter >= 60) {
                    counter -= 60;
                    PlayerFilter.playingGame(game).forEach(wp -> {
                        PlayerStatisticsMinute minuteStats = wp.getMinuteStats();
                        minuteStats.advanceMinute();
                        //remove minute stats if over 30 minutes for memory
                        if (minuteStats.getEntries().size() > 30) {
                            minuteStats.getEntries().remove(0);
                        }
                    });
                }
                PlayerFilter.playingGame(game).forEach(wp -> {
                    PlayerStatisticsSecond secondStats = wp.getSecondStats();
                    secondStats.advanceSecond();
                    //remove second stats if over 10 minutes for memory
                    if (secondStats.getEntries().size() > 60 * 10) {
                        secondStats.getEntries().remove(0);
                    }
                });
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
        game.registerGameMarker(TimerSkipAbleMarker.class, (delay) -> {
                    counter += delay / GameRunnable.SECOND;
                    timer += delay;
                }
        );

        this.game.forEachOfflineWarlordsPlayer(wp -> {
            DatabasePlayer databasePlayer = wp.getDatabasePlayer();
            if (databasePlayer.hasPermission(Permissions.STREAMER.permission)) {
                databasePlayer.getGameLogs().add(new StreamChaptersCommand.GameTime(Instant.now(), game.getMap(), wp.getSpecClass(), game.playersCount()));
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            }
            if (StreamChaptersCommand.GAME_TIMES.containsKey(wp.getUuid())) {
                StreamChaptersCommand.GAME_TIMES.get(wp.getUuid())
                                                .add(new StreamChaptersCommand.GameTime(Instant.now(), game.getMap(), wp.getSpecClass(), game.playersCount()));
            }
        });

        Warlords.getInstance().hideAndUnhidePeople();
        Game.reopenGameReferencedMenus();

        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game start done");
    }

    @Override
    public State run() {
        return null;
    }

    @Override
    @SuppressWarnings("null")
    public void end() {
        this.getGame().forEachOfflineWarlordsEntity(e -> e.setActive(false));
        ChatUtils.MessageType.WARLORDS.sendMessage(" ----- GAME END ----- ");
        ChatUtils.MessageType.WARLORDS.sendMessage("RecordGames = " + RecordGamesCommand.recordGames);
        ChatUtils.MessageType.WARLORDS.sendMessage("Force End = " + (winEvent == null));
        ChatUtils.MessageType.WARLORDS.sendMessage("Player Count = " + game.warlordsPlayers().count());
        ChatUtils.MessageType.WARLORDS.sendMessage("Players = " + game.warlordsPlayers().toList());
        ChatUtils.MessageType.WARLORDS.sendMessage("Timer = " + timer / 20 / 60 + "m");
        ChatUtils.MessageType.WARLORDS.sendMessage("Private = " + game.getAddons().contains(GameAddon.PRIVATE_GAME));
        ChatUtils.MessageType.WARLORDS.sendMessage("GameMode = " + game.getGameMode());
        ChatUtils.MessageType.WARLORDS.sendMessage("Map = " + game.getMap());
        ChatUtils.MessageType.WARLORDS.sendMessage("Game Addons = " + game.getAddons());
        ChatUtils.MessageType.WARLORDS.sendMessage("Win Event = " + (winEvent == null ? null : winEvent.getCause()));
        ChatUtils.MessageType.WARLORDS.sendMessage(" ----- GAME END ----- ");

        List<WarlordsPlayer> players = PlayerFilterGeneric.playingGameWarlordsPlayers(game).toList();
        if (players.isEmpty()) {
            ChatUtils.MessageType.GAME_DEBUG.sendMessage("No players in game, not adding game");
            return;
        }

        if (winEvent != null) {
            boolean isCompGame = game.getAddons().contains(GameAddon.PRIVATE_GAME) &&
                    !com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode()) &&
                    players.size() >= game.getGameMode().getMinPlayersToAddToDatabase() &&
                    timer >= 6000;
            //comps
            if (isCompGame) {
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Adding comp game");
                gameAdded.set(DatabaseGameBase.addGame(game, winEvent, RecordGamesCommand.recordGames));
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Done adding comp game");
            }
            //pubs or pve
            else if (players.size() >= game.getMap().getMinPlayers()) {
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Adding pub/pve game");
                gameAdded.set(DatabaseGameBase.addGame(game, winEvent, true));
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Done adding pub/pve game");
                if (!com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                    SRCalculator.recalculateSR();
                }
            }
        } else {
            if (game.getAddons().contains(GameAddon.PRIVATE_GAME) && players.size() >= 6 && timer >= 6000) {
                DatabaseGameBase.addGame(game, null, false);
                ChatUtils.MessageType.WARLORDS.sendMessage("SOME CASE");
            } else {
                ChatUtils.MessageType.WARLORDS.sendMessage("This PUB/COMP game was not added to the database and player information remained the same");
            }
        }
    }

    @Override
    public void onPlayerReJoinGame(@Nonnull Player player) {
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp == null) {
            Location spawn = Stream.concat(
                    getGame().getMarkers(SpawnLocationMarker.class).stream(),
                    getGame().getMarkers(LobbyLocationMarker.class).stream()
            ).map(LocationMarker::getLocation).collect(Utils.randomElement());
            player.teleport(spawn);
            // Spectator - delay one tick so gamemode applies after teleport
            new GameRunnable(getGame()) {

                @Override
                public void run() {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }.runTaskLater(1);
        }
        if (wp instanceof WarlordsPlayer warlordsPlayer) {
            updater.updateBasedOnGameState(warlordsPlayer);
        }
        game.forEachEnabledOption(option -> option.onPlayerReJoinGame(player));
    }

    @Override
    public void onPlayerQuitGame(OfflinePlayer player) {
        updater.removePlayer(player.getUniqueId());
    }

    @Override
    public int getTicksElapsed() {
        return this.timer;
    }

    @Nonnull
    public Game getGame() {
        return game;
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
        for (TimerResetAbleMarker marker : game.getMarkers(TimerResetAbleMarker.class)) {
            marker.reset();
        }
    }

    public PlayingStateScoreboardUpdater getUpdater() {
        return updater;
    }

}
