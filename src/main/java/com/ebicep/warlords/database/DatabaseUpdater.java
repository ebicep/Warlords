package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.games.GameService;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseUpdater {

    private static final ExecutorService VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static final AtomicReference<ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>>> PLAYERS_TO_UPDATE = new AtomicReference<>(new ConcurrentHashMap<>());

    private record PendingGameSave(DatabaseGameBase game, GamesCollections collection) {
    }

    private static final Set<PendingGameSave> PENDING_GAME_SAVES = ConcurrentHashMap.newKeySet();

    public static void clearQueue(PlayersCollections playersCollections) {
        PLAYERS_TO_UPDATE.get().remove(playersCollections);
    }

    public static void markPlayerForUpdate(DatabasePlayer databasePlayer, PlayersCollections playersCollections) {
        PLAYERS_TO_UPDATE.get().computeIfAbsent(playersCollections, k -> ConcurrentHashMap.newKeySet()).add(databasePlayer);
    }

    public static int getPendingUpdateCount() {
        return PLAYERS_TO_UPDATE.get().values().stream().mapToInt(Set::size).sum();
    }

    public static void updatePlayersBlocking(PlayerService playerService) {
        List<Future<?>> futures = updatePlayers(playerService);
        for (Future<?> future : futures) {
            try {
                future.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Timed out waiting for player update during shutdown");
                future.cancel(true);
            } catch (Exception e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Error waiting for player update to complete");
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
        }
        int pending = getPendingUpdateCount();
        if (pending > 0) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(pending + " player updates remain unsaved after shutdown flush");
        }
    }

    @Nonnull
    public static List<Future<?>> updatePlayers(PlayerService playerService) {
        if (!DatabaseHealth.isOperational() || playerService == null) {
            return List.of();
        }
        ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> toUpdate = PLAYERS_TO_UPDATE.getAndSet(new ConcurrentHashMap<>());
        List<Future<?>> updateFutures = new ArrayList<>(toUpdate.values().stream().mapToInt(Set::size).sum());
        toUpdate.forEach((playersCollections, databasePlayers) -> {
            for (DatabasePlayer databasePlayer : databasePlayers) {
                updateFutures.add(VIRTUAL_THREAD_EXECUTOR.submit(() -> {
                    DatabasePlayer toSave = Optional
                            .ofNullable(DatabaseManager.CACHED_PLAYERS.get(playersCollections).get(databasePlayer.getUuid()))
                            .orElse(databasePlayer);
                    try {
                        playerService.update(toSave, playersCollections);
                    } catch (Exception e) {
                        if (DatabaseHealth.isDuplicateKeyFailure(e) && toSave.getId() == null) {
                            playerService.reloadFromDatabase(toSave.getUuid(), playersCollections)
                                    .ifPresent(reloaded -> {
                                        DatabasePlayer canonical = DatabaseManager.cachePlayer(playersCollections, reloaded);
                                        markPlayerForUpdate(canonical, playersCollections);
                                    });
                            return;
                        }
                        markPlayerForUpdate(databasePlayer, playersCollections);
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage("Error updating player " + databasePlayer.getName() + " in collection " + playersCollections);
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                        DatabaseHealth.markUnhealthy(e);
                    }
                }));
            }
        });
        return updateFutures;
    }

    public static void updateGameAsync(DatabaseGameBase databaseGame) {
        if (!DatabaseManager.enabled) {
            return;
        }
        markGameForSave(databaseGame, GamesCollections.ALL);
        markGameForSave(databaseGame, databaseGame.getGameMode().getGamesCollections());
        flushPendingGameSaves();
    }

    public static void markGameForSave(DatabaseGameBase databaseGame, GamesCollections collection) {
        PENDING_GAME_SAVES.add(new PendingGameSave(databaseGame, collection));
    }

    public static int getPendingGameSaveCount() {
        return PENDING_GAME_SAVES.size();
    }

    public static void flushPendingGameSaves() {
        if (!DatabaseHealth.isOperational() || DatabaseManager.gameService == null || PENDING_GAME_SAVES.isEmpty()) {
            return;
        }
        List<PendingGameSave> pendingSaves = new ArrayList<>(PENDING_GAME_SAVES);
        PENDING_GAME_SAVES.clear();
        Warlords.newChain().async(() -> flushPendingGameSavesSync(DatabaseManager.gameService, pendingSaves)).execute();
    }

    public static void flushPendingGameSavesBlocking() {
        if (!DatabaseHealth.isOperational() || DatabaseManager.gameService == null || PENDING_GAME_SAVES.isEmpty()) {
            return;
        }
        List<PendingGameSave> pendingSaves = new ArrayList<>(PENDING_GAME_SAVES);
        PENDING_GAME_SAVES.clear();
        flushPendingGameSavesSync(DatabaseManager.gameService, pendingSaves);
    }

    private static void flushPendingGameSavesSync(GameService gameService, List<PendingGameSave> pendingSaves) {
        for (PendingGameSave pendingSave : pendingSaves) {
            try {
                gameService.save(pendingSave.game(), pendingSave.collection());
            } catch (Exception e) {
                PENDING_GAME_SAVES.add(pendingSave);
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Error saving game to collection " + pendingSave.collection());
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                DatabaseHealth.markUnhealthy(e);
                break;
            }
        }
    }

}
