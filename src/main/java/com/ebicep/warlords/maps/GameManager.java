package com.ebicep.warlords.maps;

import com.ebicep.warlords.util.LocationFactory;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.OfflinePlayer;

public class GameManager implements AutoCloseable {

    private final List<GameHolder> games = new ArrayList<>();
    private final LinkedList<QueueEntry> queue = new LinkedList<>();

    /**
     * Gets the list of game holders
     * @return the games holders
     */
    public List<GameHolder> getGames() {
        return games;
    }

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
                // If a game has 0 internalPlayers assigned, force end it
                next.forceEndGame(); // This mutated holder.game
            }
            if (next.getGame() == null) {
                int computedMaxPlayers = next.getMap().getMaxPlayers();
                for (GameAddon addon : entry.getRequestedGameAddons()) {
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
                // No else statement for above if, we already found a map that is running which is willing to accept us
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
        while (itr.hasNext()) {
            QueueEntry entry = itr.next();
            if (entry == null) {
                return;
            }
            GameHolder selected = findSuitableGame(entry);
            if (selected == null) {
                if (now > entry.getExpireTime()) {
                    itr.remove();
                    entry.onResult(QueueResult.EXPIRED);
                }
                // We were unable to find a suiteable game for this player
                continue;
            }
            // We found a game, mark the entry as removed
            itr.remove();
            Game game = selected.optionallyStartNewGame(entry.getRequestedGameAddons(), entry.getCategory());
            for (OfflinePlayer player : entry.getPlayers()) {
                game.addPlayer(player, false);
            }
        };
    }

    public void dropPlayerFromQueueOrGames(OfflinePlayer player) {
        this.dropPlayerFromQueueOrGames(player, false);
    }

    private void dropPlayerFromQueueOrGames(OfflinePlayer player, boolean wouldBeReplaced) {
        for (Iterator<QueueEntry> itr = queue.iterator(); itr.hasNext();) {
            QueueEntry entry = itr.next();
            if (entry.players.contains(player)) {
                itr.remove();
                entry.onResult(wouldBeReplaced ? QueueResult.REPLACED : QueueResult.CANCELLED);
            }
        }
        for (GameHolder holder : games) {
            if (holder.getGame() != null && (holder.getGame().acceptsPeople() || wouldBeReplaced)) {
                holder.getGame().removePlayer(player.getUniqueId());
            }
        }
    }
    
    public long getPlayerCount() {
        return this.games.stream().map(e -> e.getGame() == null ? 0 : e.getGame().getPlayers().size()).collect(Collectors.counting());
    }
    
    public long getPlayerCountInLobby() {
        return this.games.stream().map(e -> e.getGame() == null && e.getGame().acceptsPeople() ? 0 : e.getGame().getPlayers().size()).collect(Collectors.counting());
    }
    
    public long getQueueSize() {
        return this.queue.size();
    }
    
    public long getQueuePlayerCount() {
        return this.queue.stream().map(e -> e.getPlayers().size()).collect(Collectors.counting());
    }

    private boolean queue(QueueEntry entry) {
        if (entry.getPlayers().isEmpty()) {
            throw new IllegalArgumentException("Cannot queue an entry with 0 players");
        }
        if (queue.contains(entry)) {
            throw new IllegalArgumentException("Queue entry already exists");
        }
        boolean valid = false;
        for (GameHolder next : games) {
            if (entry.getMap() != null && entry.getMap() != next.getMap()) {
                continue; // Skip if the user wants to join a game with a different map
            }
            if (entry.getCategory() != null && next.getMap().getCategories().contains(entry.getCategory())) {
                continue; // Skip if the user wants to join a game with a different category
            }
            valid = true;
            break;
        }
        if (!valid) {
            entry.onResult(QueueResult.INVALID);
            return false;
        }
        for (OfflinePlayer p : entry.getPlayers()) {
            GameManager.this.dropPlayerFromQueueOrGames(p, true);
        }
        queue.add(entry);
        runQueue();
        return true;
    }

    private QueueResult queueNow(QueueEntry entry) {
        Consumer<QueueResult> onResult = entry.getOnResult();
        try {
            AtomicReference<QueueResult> res = new AtomicReference<>(null);
            entry.setOnResult(result -> res.set(result));
            if (!queue(entry)) {
                return QueueResult.INVALID;
            }
            runQueue();
            QueueResult val = res.get();
            if (val == null) {
                queue.remove(entry);
                val = QueueResult.EXPIRED;
            }
            if (onResult != null) {
                onResult.accept(val);
            }
            return val;
        } finally {
            entry.setOnResult(onResult);
        }
    }
    public QueueEntryBuilder newEntry(Collection<? extends OfflinePlayer> players) {
        return new QueueEntryBuilder(players, null);
    }
    public QueueEntryBuilder newEntry(Collection<? extends OfflinePlayer> players, @Nullable Consumer<QueueResult> onResult) {
        return new QueueEntryBuilder(players, onResult);
    }

    @Override
    public void close() {
        for (QueueEntry entry : queue) {
            entry.onResult(QueueResult.CLOSE);
        }
        queue.clear();
        for (GameHolder next : games) {
            next.forceEndGame();
        }
        games.clear();
    }

    public static class GameHolder {

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

        public void forceEndGame() {
            game.close();
            game = null;
        }

        @Nonnull
        private Game optionallyStartNewGame(@Nonnull EnumSet<GameAddon> requestedGameAddons, @Nullable MapCategory category) {
            if (game == null) {
                MapCategory newCategory = category != null ? category
                        : map.getCategories().get((int) (Math.random() * map.getCategories().size()));
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

        public String getName() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    private static class QueueEntry implements Comparable<QueueEntry> {

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

        public void onResult(@Nonnull QueueResult res) {
            if (onResult != null) {
                onResult.accept(res);
            }
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
        READY(true, "Your game is ready"),
        EXPIRED(false, "No game found in time"),
        CANCELLED(false, "Cancelled queueing"),
        REPLACED(false, "Replaced with another queue entry"),
        INVALID(false, "Your request to queue was invalid"),
        CLOSE(false, "The queue has been closed"),;
        private final boolean success;
        private final String message;

        private QueueResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }
        
        @Override
        public String toString() {
            return message;
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
        @Nullable
        private Consumer<GameManager.QueueResult> onResult;
        private long expiresTime = Long.MAX_VALUE;

        public QueueEntryBuilder(Collection<? extends OfflinePlayer> players, @Nullable Consumer<QueueResult> onResult) {
            this.players = new ArrayList<>(players);
            this.onResult = onResult;
        }

        public QueueEntryBuilder setPlayers(@Nonnull Collection<? extends OfflinePlayer> players) {
            this.players = new ArrayList<>(players);
            return this;
        }

        public QueueEntryBuilder setRequestedGameAddons(@Nonnull EnumSet<GameAddon> requestedGameAddons) {
            this.requestedGameAddons = requestedGameAddons.clone();
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
        
        @Nonnull
        public QueueResult queueNow() {
            return GameManager.this.queueNow(new GameManager.QueueEntry(players, Long.MIN_VALUE, requestedGameAddons, category, map, null, priority));
        }

    }
}
