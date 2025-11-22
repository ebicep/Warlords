package com.ebicep.warlords.database;

import com.ebicep.warlords.database.repositories.player.PlayerService;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseUpdater {

    private static final ExecutorService VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static final AtomicReference<ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>>> PLAYERS_TO_UPDATE = new AtomicReference<>(new ConcurrentHashMap<>());

    public static void clearQueue(PlayersCollections playersCollections) {
        PLAYERS_TO_UPDATE.get().remove(playersCollections);
    }

    public static void markPlayerForUpdate(DatabasePlayer databasePlayer, PlayersCollections playersCollections) {
        PLAYERS_TO_UPDATE.get().computeIfAbsent(playersCollections, k -> ConcurrentHashMap.newKeySet()).add(databasePlayer);
    }

    public static void updatePlayersBlocking(PlayerService playerService) {
        List<Future<?>> futures = updatePlayers(playerService);
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Error waiting for player update to complete");
                ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            }
        }
    }

    @Nonnull
    public static List<Future<?>> updatePlayers(PlayerService playerService) {
        ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> toUpdate = PLAYERS_TO_UPDATE.getAndSet(new ConcurrentHashMap<>());
        List<Future<?>> updateFutures = new ArrayList<>(toUpdate.values().stream().mapToInt(Set::size).sum());
        toUpdate.forEach((playersCollections, databasePlayers) -> {
            for (DatabasePlayer databasePlayer : databasePlayers) {
                updateFutures.add(VIRTUAL_THREAD_EXECUTOR.submit(() -> {
                    try {
                        playerService.update(databasePlayer, playersCollections);
                    } catch (Exception e) {
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage("Error updating player " + databasePlayer.getName() + " in collection " + playersCollections);
                        ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
                    }
                }));
            }
        });
        return updateFutures;
    }

}
