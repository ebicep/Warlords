package com.ebicep.warlords.maps;

import com.ebicep.warlords.maps.state.PreLobbyState;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;

public class GameManager {

    private final List<GameHolder> games = new ArrayList<>();
    private final LinkedList<QueueEntry> queue = new LinkedList<>();
    
    @Nullable
    private GameHolder findSuitableGame(@Nonnull QueueEntry entry) {
        GameHolder selected = null;
        int newGamesSeen = 0;
        for(GameHolder next : games) {
            if (entry.getMap() != null && entry.getMap() != next.getMap()) {
                continue; // Skip if the user wants to join a game with a different map
            }
            if (entry.getCategory()!= null && entry.getCategory() != next.getMap().getCategory()) {
                continue; // Skip if the user wants to join a game with a different category
            }
            if (next.getGame() != null && next.getGame().playersCount() == 0) {
                next.forceEndGame();
            }
            if (next.getGame() == null) {
                // The game has not started yet
                if (next.getMap().getMaxPlayers() < entry.getPlayers().size()) {
                    continue; // The party would not fit into this map
                }
                newGamesSeen++;
                if (selected == null) {
                    selected = next;
                } else if (selected.getGame() == null) {
                    // Randomly assing the player a new game instance (within the above bounds checks)
                    if (Math.random() < 1 / newGamesSeen) {
                        selected = next;
                    }
                }
                // No else statement for above if, we already found a better candidate map
            } else {
                if (!(next.getGame().getState() instanceof PreLobbyState)) {
                    continue;
                }
                if (!entry.getRequestedGameAddons().equals(next.getGame().getAddons()) || entry.getRequestedGameAddons().contains(GameAddon.PRIVATE_GAME)) {
                    continue;
                }
                if (next.getMap().getMaxPlayers() - next.getGame().playersCount() < entry.getPlayers().size()) {
                    continue; // The party would not fit into this map
                }
                if (selected == null) {
                    selected = next;
                } else if (selected.getGame() == null) {
                    selected = next;
                } else {
                    if (selected.getGame().createdAt() < next.getGame().createdAt()) {
                        continue;
                    }
                    selected = next;
                }
            }
        }
        return selected;
    }
    
    private void runQueue() {
        long now = System.currentTimeMillis();
        Iterator<QueueEntry> itr = queue.iterator();
        while(itr.hasNext()) {
            QueueEntry entry = itr.next();
            if (entry == null) {
                return;
            }
            GameHolder selected = findSuitableGame(entry);
            if (selected == null) {
                if (now > entry.getExpireTime()) {
                    itr.remove();
                    if (entry.getOnResult() != null) {
                        entry.getOnResult().accept(QueueResult.EXPIRED);
                    }
                }
                // We were unable to find a suiteable game for this player
                continue;
            }
            // We found a game, mark the entry as removed
            itr.remove();
            Game game = selected.optionallyStartNewGame(entry.requestedGameAddons);
            for (OfflinePlayer player : entry.getPlayers()) {
                game.addPlayer(player, Team.RED);
            }
        };
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
        
        @Nonnull
        private final String name;

        public GameHolder(GameMap map, String name) {
            this.map = map;
            this.name = name;
        }

        public GameMap getMap() {
            return map;
        }

        @Nullable
        public Game getGame() {
            return game;
        }

        private void forceEndGame() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Nonnull
        private Game optionallyStartNewGame(EnumSet<GameAddon> requestedGameAddons) {
            if (game == null) {
                game = new Game(requestedGameAddons);
            }
            if (!game.getAddons().equals(requestedGameAddons)) {
                throw new IllegalArgumentException('[' + name + "] The requested game addons do not match the actaul game addons: " + requestedGameAddons + " vs " + game.getAddons());
            }
            return game;
        }

    }

    private static class QueueEntry implements Comparable<QueueEntry>{
        private static final AtomicInteger SEQUENCE = new AtomicInteger();

        @Nonnull
        private final List<OfflinePlayer> players;
        private final long expireTime;
        @Nonnull
        private final EnumSet<GameAddon> requestedGameAddons;
        @Nullable
        private final MapCategory category;
        @Nullable
        private final GameMap map;
        @Nullable
        private final Consumer<QueueResult> onResult;
        private final int priority;
        private final int insertionId;

        public QueueEntry(
                @Nonnull List<OfflinePlayer> players,
                long expiresTime,
                @Nonnull EnumSet<GameAddon> requestedGameAddons,
                @Nullable MapCategory category,
                @Nullable GameMap map,
                @Nullable Consumer<QueueResult> onResult,
                int priority
        ) {
            this.players = players;
            this.expireTime = expiresTime;
            this.requestedGameAddons = requestedGameAddons;
            this.category = category;
            this.map = map;
            this.onResult = onResult;
            this.priority = priority;
            this.insertionId = SEQUENCE.incrementAndGet();
        }

        @Nonnull
        public List<OfflinePlayer> getPlayers() {
            return players;
        }

        public long getExpireTime() {
            return expireTime;
        }

        @Nonnull
        public EnumSet<GameAddon> getRequestedGameAddons() {
            return requestedGameAddons;
        }

        public MapCategory getCategory() {
            return category;
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
