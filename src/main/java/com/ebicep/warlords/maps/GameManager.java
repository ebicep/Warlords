package com.ebicep.warlords.maps;

import com.ebicep.warlords.util.LocationFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;

public class GameManager implements AutoCloseable {

    private final List<GameHolder> games = new ArrayList<>();
    private final LinkedList<QueueEntry> queue = new LinkedList<>();
    
    @Nullable
    private GameHolder findSuitableGame(@Nonnull QueueEntry entry) {
        GameHolder selected = null;
        int newGamesSeen = 0;
        for (GameHolder next : games) {
            if (entry.getMap() != null && entry.getMap() != next.getMap()) {
                continue; // Skip if the user wants to join a game with a different map
            }
            if (entry.getCategory() != null && next.getMap().getCategories().contains(entry.getCategory())) {
                continue; // Skip if the user wants to join a game with a different category
            }
            if (next.getGame() != null && next.getGame().playersCount() == 0) {
                // If a game has 0 players assigned, force end it
                next.forceEndGame();
            }
            if (next.getGame() == null) {
                int computedMaxPlayers = next.getMap().getMaxPlayers();
                for(GameAddon addon : entry.getRequestedGameAddons()) {
                    computedMaxPlayers = addon.getMaxPlayers(next.getMap(), computedMaxPlayers);
                }
                // The game has not started yet
                if (computedMaxPlayers < entry.getPlayers().size()) {
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
                if (!next.getGame().acceptsPeople()) {
                    continue;
                }
                if (!entry.getRequestedGameAddons().equals(next.getGame().getAddons())) {
                    continue;
                }
                if (next.getGame().getMaxPlayers() - next.getGame().playersCount() < entry.getPlayers().size()) {
                    continue; // The party would not fit into this map
                }
                if (entry.getCategory() != null && next.getGame().getCategory() != entry.getCategory()) {
                    continue; // Skip if the user wants to join a game with a different category
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
            Game game = selected.optionallyStartNewGame(entry.getRequestedGameAddons(), entry.getCategory());
            for (OfflinePlayer player : entry.getPlayers()) {
                game.addPlayer(player);
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
        runQueue();
    }
    private QueueResult queueNow(QueueEntry entry) {
        Consumer<QueueResult> onResult = entry.getOnResult();
        AtomicReference<QueueResult> res = new AtomicReference<>(null);
        entry.setOnResult(result -> res.set(result));
        queue.add(entry);
        runQueue();
        QueueResult val = res.get();
        if (val == null) {
            queue.remove(entry);
            val = QueueResult.EXPIRED;
        }
        if(onResult != null) {
            onResult.accept(val);
        }
        return val;
    }

    public QueueEntryBuilder newEntry(List<OfflinePlayer> players) {
        return new QueueEntryBuilder(players);
    }

    public AsyncQueueEntryBuilder newEntry(List<OfflinePlayer> players, @Nonnull Consumer<QueueResult> onResult) {
        return new AsyncQueueEntryBuilder(players, onResult);
    }
    

    @Override
    public void close() {
        for(QueueEntry entry : queue) {
            
        }
    }

    private static class GameHolder {

        @Nullable
        private Game game;
        
        @Nonnull
        private final GameMap map;
        
        @Nonnull
        private final LocationFactory locations;
        
        @Nonnull
        private final String name;

        public GameHolder(GameMap map, LocationFactory locations, String name) {
            this.map = map;
            this.locations = locations;
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
            game.close();
        }

        @Nonnull
        private Game optionallyStartNewGame(@Nonnull EnumSet<GameAddon> requestedGameAddons, @Nullable MapCategory category) {
            if (game == null) {
                MapCategory newCategory = category != null ? category :
                        map.getCategories().get((int) (Math.random() * map.getCategories().size()));
                game = new Game(requestedGameAddons, map, newCategory, locations);
            }
            if (!game.getAddons().equals(requestedGameAddons)) {
                throw new IllegalArgumentException(
                        '[' + name + "] The requested game addons do not match the actaul game addons: " + requestedGameAddons + " vs " + game.getAddons()
                );
            }
            if (!game.getCategory().equals(category)) {
                throw new IllegalArgumentException(
                        '[' + name + "] The requested game category do not match the actaul game category: " + category + " vs " + game.getCategory()
                );
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
        private Consumer<QueueResult> onResult;
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
            this.players = Objects.requireNonNull(players, "players");
            this.expireTime = expiresTime;
            this.requestedGameAddons = Objects.requireNonNull(requestedGameAddons, "requestedGameAddons");
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

        public void setOnResult(Consumer<QueueResult> onResult) {
            this.onResult = onResult;
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
        READY(true),
        EXPIRED(false),
        CANCELLED(false),
        REPLACED(false),
        TOO_BIG(false),
        ;
        private final boolean success;

        private QueueResult(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }
    
    public class QueueEntryBuilder {

        @Nonnull
        protected List<OfflinePlayer> players;
        @Nonnull
        protected EnumSet<GameAddon> requestedGameAddons = EnumSet.noneOf(GameAddon.class);
        @Nullable
        protected MapCategory category = null;
        @Nullable
        protected GameMap map = null;
        protected int priority = 0;

        public QueueEntryBuilder(@Nonnull List<OfflinePlayer> players) {
            this.players = players;
        }

        public QueueEntryBuilder setPlayers(@Nonnull List<OfflinePlayer> players) {
            this.players = players;
            return this;
        }

        public QueueEntryBuilder setRequestedGameAddons(@Nonnull EnumSet<GameAddon> requestedGameAddons) {
            this.requestedGameAddons = requestedGameAddons;
            return this;
        }

        public QueueEntryBuilder setCategory(@Nullable MapCategory category) {
            this.category = category;
            return this;
        }

        public QueueEntryBuilder setMap(@Nullable GameMap map) {
            this.map = map;
            return this;
        }

        public QueueEntryBuilder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        @Nonnull
        public List<OfflinePlayer> getPlayers() {
            return players;
        }

        @Nonnull
        public EnumSet<GameAddon> getRequestedGameAddons() {
            return requestedGameAddons;
        }

        @Nullable
        public MapCategory getCategory() {
            return category;
        }

        @Nullable
        public GameMap getMap() {
            return map;
        }

        public int getPriority() {
            return priority;
        }

        @Nonnull
        public QueueResult queueNow() {
            return GameManager.this.queueNow(new GameManager.QueueEntry(players, Long.MIN_VALUE, requestedGameAddons, category, map, null, priority));
        }

    }
    
    public class AsyncQueueEntryBuilder extends QueueEntryBuilder {
        @Nonnull
        private Consumer<GameManager.QueueResult> onResult;
        private long expiresTime = Long.MAX_VALUE;

        public AsyncQueueEntryBuilder(List<OfflinePlayer> players, @Nonnull Consumer<QueueResult> onResult) {
            super(players);
            this.onResult = onResult;
        }

        public QueueEntryBuilder setExpiresTime(long expiresTime) {
            this.expiresTime = expiresTime;
            return this;
        }

        public QueueEntryBuilder setOnResult(@Nonnull Consumer<GameManager.QueueResult> onResult) {
            this.onResult = onResult;
            return this;
        }

        @Nonnull
        public Consumer<QueueResult> getOnResult() {
            return onResult;
        }

        public long getExpiresTime() {
            return expiresTime;
        }

        public void queue() {
            GameManager.this.queue(new GameManager.QueueEntry(players, expiresTime, requestedGameAddons, category, map, onResult, priority));
        }
    }
}


