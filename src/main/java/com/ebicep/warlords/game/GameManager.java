package com.ebicep.warlords.game;

import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class GameManager implements AutoCloseable {

    private final List<GameHolder> games = new ArrayList<>();
    private final LinkedList<QueueEntry> queue = new LinkedList<>();

    /**
     * Gets the list of game holders
     *
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
            if (entry.getCategory() != null && !next.getMap().getCategories().contains(entry.getCategory())) {
                continue; // Skip if the user wants to join a game with a different category
            }
            if (next.getGame() != null && (next.getGame().playersCount() == 0 || next.getGame().isClosed())) {
                // If a game has 0 internalPlayers assigned, force end it
                next.forceEndGame(); // This mutates holder.game
            }
            Game game = next.getGame();
            if (game == null) {
                int computedMaxPlayers = next.getMap().getMaxPlayers();
                for (GameAddon addon : entry.getRequestedGameAddons()) {
                    if (!addon.canCreateGame(next)) {
                        continue;
                    }
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
                    // Randomly assigning the player a new game instance (within the above bounds checks)
                    if (Math.random() * newGamesSeen < 1) {
                        selected = next;
                    }
                }
                // No else statement for above if, we already found a map that is running which is willing to accept us
            } else {
                if (!game.acceptsPeople()) {
                    continue;
                }
                if (!entry.getRequestedGameAddons().equals(game.getAddons())) {
                    continue;
                }
                if (game.getMaxPlayers() - game.playersCount() < entry.getPlayers().size()) {
                    continue; // The party would not fit into this map
                }
                if (entry.getCategory() != null && game.getGameMode() != entry.getCategory()) {
                    continue; // Skip if the user wants to join a game with a different category
                }
                if (selected == null) {
                    selected = next;
                } else if (selected.getGame() == null) {
                    selected = next;
                } else {
                    if (selected.getGame().createdAt() < game.createdAt()) {
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
            GameHolder selected;
            try {
                selected = findSuitableGame(entry);
            } catch(Throwable e) {
				entry.onResult(QueueResult.ERROR_FIND_GAME, null);
                throw e;
			}
            if (selected == null) {
                if (now > entry.getExpireTime()) {
                    itr.remove();
                    entry.onResult(QueueResult.EXPIRED, null);
                }
                // We were unable to find a suiteable game for this player
                continue;
            }
            // We found a game, mark the entry as removed
            itr.remove();
			boolean isNewGame = selected.getGame() == null;
			Game game;
			try {
				game = selected.optionallyStartNewGame(entry.getRequestedGameAddons(), entry.getCategory());
			} catch(Throwable e) {
				entry.onResult(QueueResult.ERROR_NEW_GAME, null);
				throw e;
			}
            entry.onResult(isNewGame ? QueueResult.READY_NEW : QueueResult.READY_JOIN, game);
            for (OfflinePlayer player : entry.getPlayers()) {
                game.addPlayer(player, false);
            }
        }
    }

    public void dropPlayerFromQueueOrGames(OfflinePlayer player) {
        this.dropPlayerFromQueueOrGames(player, false);
    }

    private void dropPlayerFromQueueOrGames(OfflinePlayer player, boolean wouldBeReplaced) {
        for (Iterator<QueueEntry> itr = queue.iterator(); itr.hasNext();) {
            QueueEntry entry = itr.next();
            if (entry.players.contains(player)) {
                itr.remove();
                entry.onResult(wouldBeReplaced ? QueueResult.REPLACED : QueueResult.CANCELLED, null);
            }
        }
        for (GameHolder holder : games) {
            if (holder.getGame() != null && (holder.getGame().acceptsPeople() || wouldBeReplaced)) {
                holder.getGame().removePlayer(player.getUniqueId());
            }
        }
    }

    public long getPlayerCount() {
        return this.games.stream().mapToInt(e -> e.getGame() == null ? 0 : e.getGame().getPlayers().size()).sum();
    }

    public long getPlayerCountInLobby() {
        return this.games.stream().mapToInt(e -> {
            Game game = e.getGame();
            if (game == null) {
                return 0;
            }
            if (!game.acceptsPeople()) {
                return 0;
            }
            return game.getPlayers().size();
        }).sum();
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
        boolean invalidOversize = false;
        boolean invalidMapCategory = false;
        for (GameHolder next : games) {
            if (entry.getMap() != null && entry.getMap() != next.getMap()) {
                continue; // Skip if the user wants to join a game with a different map
            }
            if (entry.getCategory() != null && !next.getMap().getCategories().contains(entry.getCategory())) {
                invalidMapCategory = true;
                continue; // Skip if the user wants to join a game with a different category
            }
            int computedMaxPlayers = next.getMap().getMaxPlayers();
            for (GameAddon addon : entry.getRequestedGameAddons()) {
                if (!addon.canCreateGame(next)) {
                    continue;
                }
                computedMaxPlayers = addon.getMaxPlayers(next.getMap(), computedMaxPlayers);
            }

            // The game has not started yet
            if (computedMaxPlayers < entry.getPlayers().size()) {
                invalidOversize = true;
                continue; // The party would not fit into this map
            }
            valid = true;
            break;
        }
        if (!valid) {
            entry.onResult(
                    invalidOversize ? QueueResult.INVALID_OVERSIZE :
                    invalidMapCategory ? QueueResult.INVALID_MAP_CATEGORY :
                    QueueResult.INVALID_GENERIC,
                    null
            );
            return false;
        }
        for (OfflinePlayer p : entry.getPlayers()) {
            GameManager.this.dropPlayerFromQueueOrGames(p, true);
        }
        boolean inserted = false;
        ListIterator<QueueEntry> listIterator = queue.listIterator(queue.size());
        while (listIterator.hasPrevious()) {
            QueueEntry previous = listIterator.previous();
            if (previous.compareTo(entry) > 0) { // TODO verify if > is the correct operator here
                listIterator.add(entry);
                inserted = true;
                break;
            }
        }
        if (!inserted) {
            listIterator.add(entry);
        }
        runQueue();
        return true;
    }

    private Pair<QueueResult, Game> queueNow(QueueEntry entry) {
        BiConsumer<QueueResult, Game> onResult = entry.getOnResult();
        try {
            AtomicReference<QueueResult> res = new AtomicReference<>(null);
            AtomicReference<Game> game = new AtomicReference<>(null);
            entry.setOnResult((result, g) -> {
                    res.set(result);
                    game.set(g);
            });
            if (!queue(entry)) {
                return new Pair<>(QueueResult.INVALID_GENERIC, null);
            }// BiConsumer<QueueResult, Game>
            runQueue();
            QueueResult val = res.get();
            Game g = game.get();
            if (val == null) {
                val = QueueResult.EXPIRED;
            }
            if (onResult != null) {
                onResult.accept(val, g);
            }
            return new Pair<>(val, g);
        } finally {
            queue.remove(entry);
            entry.setOnResult(onResult);
        }
    }

    public QueueEntryBuilder newEntry(Collection<? extends OfflinePlayer> players) {
        return new QueueEntryBuilder(players, null);
    }

    public QueueEntryBuilder newEntry(Collection<? extends OfflinePlayer> players, @Nullable BiConsumer<QueueResult, Game> onResult) {
        return new QueueEntryBuilder(players, onResult);
    }

    @Nonnull
    public Optional<Game> getPlayerGame(UUID player) {
        return this.games.stream().filter(e -> e.getGame() != null && e.getGame().hasPlayer(player)).map(e -> e.getGame()).findAny();
    }

    @Override
    public void close() {
        for (QueueEntry entry : queue) {
            entry.onResult(QueueResult.CLOSE, null);
        }
        queue.clear();
        for (GameHolder next : games) {
            next.forceEndGame();
        }
        games.clear();
    }

    public void addGameHolder(String name, GameMap map, World world) {
        addGameHolder(name, map, new LocationFactory(world));
    }

    public void addGameHolder(String name, GameMap map, LocationFactory locations) {
        this.games.add(new GameHolder(map, locations, name));
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
            if (game != null) {
                game.close();
                game = null;
            }
        }

        @Nonnull
        private Game optionallyStartNewGame(@Nonnull EnumSet<GameAddon> requestedGameAddons, @Nullable GameMode category) {
            if (game == null) {
                GameMode newCategory = category != null ? category
                        : map.getCategories().get((int) (Math.random() * map.getCategories().size()));
                game = new Game(requestedGameAddons, map, newCategory, locations);
                game.start();
            }
            if (!game.getAddons().equals(requestedGameAddons)) {
                throw new IllegalArgumentException(
                        '[' + name + "] The requested game addons do not match the actual game addons: " + requestedGameAddons + " vs " + game.getAddons()
                );
            }
            if (category != null && !game.getGameMode().equals(category)) {
                throw new IllegalArgumentException(
                        '[' + name + "] The requested game category do not match the actual game category: " + category + " vs " + game.getGameMode()
                );
            }
            return game;
        }

        public String getName() {
            return name;
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
        private final GameMode category;
        @Nullable
        private final GameMap map;
        @Nullable
        private BiConsumer<QueueResult, Game> onResult;
        private final int priority;
        private final int insertionId;

        public QueueEntry(
                @Nonnull List<OfflinePlayer> players,
                long expiresTime,
                @Nonnull EnumSet<GameAddon> requestedGameAddons,
                @Nullable GameMode category,
                @Nullable GameMap map,
                @Nullable BiConsumer<QueueResult, Game> onResult,
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

        public GameMode getCategory() {
            return category;
        }

        @Nullable
        public GameMap getMap() {
            return map;
        }

        @Nullable
        public BiConsumer<QueueResult, Game> getOnResult() {
            return onResult;
        }

        public void onResult(@Nonnull QueueResult res, @Nullable Game game) {
            if (onResult != null) {
                onResult.accept(res, game);
            }
        }

        public void setOnResult(BiConsumer<QueueResult, Game> onResult) {
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
        READY_JOIN("You have joined an existing game."),
        READY_NEW("A new game has been made for you."),
        ERROR_FIND_GAME("We were unable to create a new game for you because of an internal error. Please report this."),
        ERROR_NEW_GAME("We were unable to find a new for you because of an internal error. Please report this."),
        EXPIRED("No game found in time"),
        CANCELLED("Cancelled queueing"),
        REPLACED("Replaced with another queue entry"),
        INVALID_GENERIC("Your request to queue was invalid because of an unknown reason. Please report this."),
        INVALID_OVERSIZE("Your request to queue was invalid because your party was too big for the specified map/game."),
        INVALID_MAP_CATEGORY("Your request to queue was invalid because the combination of map/category was not found."),
        CLOSE("The queue has been closed"),;
        private final String message;

        QueueResult(String message) {
            this.message = message;
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
        protected GameMode category = null;
        @Nullable
        protected GameMap map = null;
        protected int priority = 0;
        @Nullable
        private BiConsumer<QueueResult, Game> onResult;
        private long expiresTime = Long.MAX_VALUE;

        public QueueEntryBuilder(Collection<? extends OfflinePlayer> players, @Nullable BiConsumer<QueueResult, Game> onResult) {
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

        public QueueEntryBuilder setRequestedGameAddons(@Nonnull GameAddon... rga) {
            return setRequestedGameAddons(rga.length == 0 ? EnumSet.noneOf(GameAddon.class) : EnumSet.copyOf(Arrays.asList(rga)));
        }

        public QueueEntryBuilder setCategory(@Nullable GameMode category) {
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
        public GameMode getCategory() {
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

        public QueueEntryBuilder setOnResult(@Nonnull BiConsumer<QueueResult, Game> onResult) {
            this.onResult = onResult;
            return this;
        }

        @Nonnull
        public BiConsumer<QueueResult, Game> getOnResult() {
            return onResult;
        }

        public long getExpiresTime() {
            return expiresTime;
        }

        public void queue() {
            GameManager.this.queue(new GameManager.QueueEntry(players, expiresTime, requestedGameAddons, category, map, onResult, priority));
        }

        @Nonnull
        public Pair<QueueResult, Game> queueNow() {
            return GameManager.this.queueNow(new GameManager.QueueEntry(players, Long.MIN_VALUE, requestedGameAddons, category, map, null, priority));
        }

    }
}
