package com.ebicep.warlords.maps;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;

public class GameManager {

    private final List<GameHolder> games = new ArrayList<>();
    private final Queue<QueueEntry> queue = new PriorityQueue<>();
    
    private void runQueue() {
        do {
            QueueEntry entry = queue.peek();
            if(entry == null) {
                return;
            }
            GameHolder selected = null;
            int newGamesSeen = 0;
            for(GameHolder next : games) {
                if (entry.getMap() == null || entry.getMap() == )
                if (next.getGame() == null) {
                    if (selected == null) {
                        selected = next;
                    } else if (selected.getGame() == null) {
                        
                    }
                }
            }
        } while (true);
    }

    public void dropPlayerFromQueue(OfflinePlayer player) {
        dropPlayerFromQueue(player, false);
    }
    private void dropPlayerFromQueue(OfflinePlayer player, boolean wouldBeReplaced) {

    }
    private void queue(QueueEntry entry) {
        queue.add(entry);
    }

    public void queuePeople(List<OfflinePlayer> players, long expire, Runnable onFailure, Runnable onSuccess, boolean privateGame) {

    }

    private static class GameHolder {

        @Nullable
        private Game game;
        
        @Nonnull
        private final GameMap map;

        public GameHolder(GameMap map) {
            this.map = map;
        }

        public GameMap getMap() {
            return map;
        }

        @Nullable
        public Game getGame() {
            return game;
        }

    }

    private static class QueueEntry implements Comparable<QueueEntry>{

        @Nonnull
        private final List<OfflinePlayer> players;
        private final long expireTime;
        @Nonnull
        private final Set<GameAddon> requestedGameAddons;
        @Nullable
        private final GameMap map;
        @Nullable
        private final Consumer<QueueResult> onResult;
        private final int priority;
        private final int insertionId;

        public QueueEntry(
                @Nonnull List<OfflinePlayer> players,
                long expiresTime,
                @Nonnull Set<GameAddon> requestedGameAddons,
                @Nullable GameMap map,
                @Nullable Consumer<QueueResult> onResult,
                int priority,
                int insertionId
        ) {
            this.players = players;
            this.expireTime = expiresTime;
            this.requestedGameAddons = requestedGameAddons;
            this.map = map;
            this.onResult = onResult;
            this.priority = priority;
            this.insertionId = insertionId;
        }

        @Nonnull
        public List<OfflinePlayer> getPlayers() {
            return players;
        }

        public long getExpireTime() {
            return expireTime;
        }

        @Nonnull
        public Set<GameAddon> getRequestedGameAddons() {
            return requestedGameAddons;
        }

        @Nullable
        public GameMap getMap() {
            return map;
        }

        @Nullable
        public Consumer<QueueResult> getOnResult() {
            return onResult;
        }

        @Override
        public int compareTo(QueueEntry o) {
            int c = Integer.compare(this.priority, o.priority);
            if (c != 0) {
                return c;
            }
            return Integer.compare(this.insertionId, o.insertionId);
        }

    }

    public enum QueueResult {
        READY,
        EXPIRED,
        CANCELLED,
        REPLACED,
    }
}
