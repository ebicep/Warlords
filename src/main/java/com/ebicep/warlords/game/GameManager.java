package com.ebicep.warlords.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.channels.OverlappingFileLockException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class GameManager implements AutoCloseable {

    public static boolean gameStartingDisabled = false;
    /** Chunk radius around MainLobby spawn kept loaded via plugin tickets. */
    private static final int MAIN_LOBBY_WARM_CHUNK_RADIUS = 15;
    private static final long IDLE_WORLD_UNLOAD_DELAY_TICKS = 60 * 60 * 20L;
    private static final long IDLE_WORLD_UNLOAD_CHECK_PERIOD_TICKS = 60 * 20L;
    private final List<GameHolder> games = new ArrayList<>();
    private final LinkedList<QueueEntry> queue = new LinkedList<>();
    private final Map<String, BukkitTask> pendingWorldUnloads = new HashMap<>();

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
            if (entry.getCategory() != null && !next.getMap().getGameModes().contains(entry.getCategory())) {
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
            } catch (Throwable e) {
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
            } catch (Throwable e) {
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
        for (Iterator<QueueEntry> itr = queue.iterator(); itr.hasNext(); ) {
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

    public long getPlayerCount(GameMode gameMode) {
        return this.games.stream()
                         .filter(gameHolder -> gameMode == null || (gameHolder.getGame() != null && gameHolder.getGame().getGameMode() == gameMode))
                         .mapToInt(e -> e.getGame() == null ? 0 : (int) e.getGame().warlordsPlayers().count()).sum();
    }

    public long getPlayerCountInLobby(GameMode gameMode) {
        return this.games.stream()
                         .filter(gameHolder -> gameMode == null || (gameHolder.getGame() != null && gameHolder.getGame().getGameMode() == gameMode))
                         .mapToInt(e -> {
                             Game game = e.getGame();
                             if (game == null) {
                                 return 0;
                             }
                             if (!game.isState(PreLobbyState.class)) {
                                 return 0;
                             }
                             return game.getPlayers().size();
                         }).sum();
    }

    public long getQueueSize() {
        return this.queue.size();
    }

    public long getQueuePlayerCount() {
        return this.queue.stream().map(e -> e.getPlayers().size()).count();
    }

    private boolean queue(QueueEntry entry) {
//        if (entry.getPlayers().isEmpty()) {
//            throw new IllegalArgumentException("Cannot queue an entry with 0 players");
//        }
        if (queue.contains(entry)) {
            throw new IllegalArgumentException("Queue entry already exists");
        }
        if (entry.getMap() != null) {
            if (!ensureGameHoldersLoaded(entry.getMap())
                    && !hasJoinableGame(entry.getMap(), entry.getCategory())) {
                entry.onResult(QueueResult.ERROR_NO_WORLD_CAPACITY, null);
                return false;
            }
        } else if (!ensureEligibleGameHoldersLoaded(entry.getCategory())
                && !hasJoinableGame(null, entry.getCategory())) {
            entry.onResult(QueueResult.ERROR_NO_WORLD_CAPACITY, null);
            return false;
        }
        boolean valid = false;
        boolean invalidOversize = false;
        boolean invalidMapCategory = false;
        for (GameHolder next : games) {
            if (entry.getMap() != null && entry.getMap() != next.getMap()) {
                continue; // Skip if the user wants to join a game with a different map
            }
            if (entry.getCategory() != null && !next.getMap().getGameModes().contains(entry.getCategory())) {
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
                QueueResult val = res.get();
                if (val == null) {
                    val = QueueResult.INVALID_GENERIC;
                }
                if (onResult != null) {
                    onResult.accept(val, null);
                }
                return new Pair<>(val, null);
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
        return this.games.stream().filter(e -> e.getGame() != null && e.getGame().hasPlayer(player)).map(GameHolder::getGame).findAny();
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
        for (BukkitTask task : pendingWorldUnloads.values()) {
            task.cancel();
        }
        pendingWorldUnloads.clear();
        games.clear();
    }

    /**
     * After a game ends, waits until the world has no players, then unloads it after
     * {@link #IDLE_WORLD_UNLOAD_DELAY_TICKS} of continuous emptiness and removes the holder.
     * No-op for MainLobby. Cancelled if a new game starts on the same holder.
     */
    public void scheduleIdleWorldUnload(@Nonnull GameHolder holder) {
        if (holder.getName().equals("MainLobby") || !Warlords.getInstance().isEnabled()) {
            return;
        }
        cancelIdleWorldUnload(holder.getName());
        BukkitTask task = new BukkitRunnable() {
            private long emptyTicks = -1;

            @Override
            public void run() {
                if (!games.contains(holder) || holder.getGame() != null) {
                    cancelIdleWorldUnload(holder.getName());
                    return;
                }
                World world = Bukkit.getWorld(holder.getName());
                if (world == null) {
                    ChatUtils.MessageType.GAME.sendErrorMessage(
                            "World " + holder.getName() + " is already unloaded (possibly externally); removing game holder."
                    );
                    games.remove(holder);
                    cancelIdleWorldUnload(holder.getName());
                    return;
                }
                if (!world.getPlayers().isEmpty()) {
                    emptyTicks = -1;
                    return;
                }
                if (emptyTicks < 0) {
                    emptyTicks = 0;
                } else {
                    emptyTicks += IDLE_WORLD_UNLOAD_CHECK_PERIOD_TICKS;
                }
                if (emptyTicks >= IDLE_WORLD_UNLOAD_DELAY_TICKS) {
                    unloadIdleWorld(holder);
                    cancelIdleWorldUnload(holder.getName());
                }
            }
        }.runTaskTimer(Warlords.getInstance(), IDLE_WORLD_UNLOAD_CHECK_PERIOD_TICKS, IDLE_WORLD_UNLOAD_CHECK_PERIOD_TICKS);
        pendingWorldUnloads.put(holder.getName(), task);
    }

    public void cancelIdleWorldUnload(@Nonnull String holderName) {
        BukkitTask task = pendingWorldUnloads.remove(holderName);
        if (task != null) {
            task.cancel();
        }
    }

    private void unloadIdleWorld(@Nonnull GameHolder holder) {
        if (holder.getGame() != null || !games.contains(holder)) {
            return;
        }
        World world = Bukkit.getWorld(holder.getName());
        if (world == null) {
            ChatUtils.MessageType.GAME.sendErrorMessage(
                    "World " + holder.getName() + " is already unloaded (possibly externally); removing game holder."
            );
            games.remove(holder);
            ChatUtils.MessageType.GAME.sendMessage("Unloaded map " + holder.getName() + " and removed game holder.");
            return;
        }
        if (!world.getPlayers().isEmpty()) {
            return;
        }
        if (Bukkit.isTickingWorlds()) {
            ChatUtils.MessageType.GAME.sendMessage(
                    "Deferring unload of " + holder.getName() + " until worlds are not ticking."
            );
            new BukkitRunnable() {
                @Override
                public void run() {
                    unloadIdleWorld(holder);
                }
            }.runTaskLater(Warlords.getInstance(), 1L);
            return;
        }
        world.removePluginChunkTickets(Warlords.getInstance());
        ChatUtils.MessageType.GAME.sendMessage("Unloading idle map " + holder.getName() + ".");
        if (!Bukkit.unloadWorld(world, false)) {
            ChatUtils.MessageType.GAME.sendErrorMessage("Failed to unload world " + holder.getName());
            return;
        }
        games.remove(holder);
        ChatUtils.MessageType.GAME.sendMessage("Unloaded map " + holder.getName() + " and removed game holder.");
    }

    public void addGameHolder(String name, GameMap map) {
        World world = loadWorldIfPresent(name);
        if (world == null) {
            ChatUtils.MessageType.GAME.sendErrorMessage("Could not find game world " + name);
            return;
        }
        if ("MainLobby".equals(name)) {
            keepMainLobbyChunksWarm(world);
        }
        this.addGameHolder(name, map, world);
    }

    public void addGameHolder(String name, GameMap map, World world) {
        addGameHolder(name, map, new LocationFactory(world));
    }

    public void addGameHolder(String name, GameMap map, LocationFactory locations) {
        this.games.add(new GameHolder(map, locations, name));
    }

    /**
     * Holds MainLobby spawn chunks loaded with plugin tickets (spawn chunks no longer exist).
     */
    private static void keepMainLobbyChunksWarm(@Nonnull World world) {
        Location spawn = world.getSpawnLocation();
        int centerX = spawn.getBlockX() >> 4;
        int centerZ = spawn.getBlockZ() >> 4;
        for (int x = centerX - MAIN_LOBBY_WARM_CHUNK_RADIUS; x <= centerX + MAIN_LOBBY_WARM_CHUNK_RADIUS; x++) {
            for (int z = centerZ - MAIN_LOBBY_WARM_CHUNK_RADIUS; z <= centerZ + MAIN_LOBBY_WARM_CHUNK_RADIUS; z++) {
                world.addPluginChunkTicket(x, z, Warlords.getInstance());
            }
        }
    }

    /**
     * Ensures at least one idle game holder exists for the map, loading a single world
     * instance from disk if needed. Does not preload every {@code fileName-i} copy.
     *
     * @return true if an idle holder exists for the map after this call
     */
    public boolean ensureGameHoldersLoaded(@Nonnull GameMap map) {
        boolean hasIdleHolder = games.stream()
                .anyMatch(holder -> holder.getMap() == map && holder.getGame() == null);
        if (hasIdleHolder) {
            return true;
        }
        for (String mapName : map.getWorldInstanceNames()) {
            if (hasGameHolder(mapName, map)) {
                continue;
            }
            if (ensureGameHolderLoaded(mapName, map)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ensures at least one idle holder exists among maps eligible for the given category.
     * Loads at most one new world per attempted map, stopping when any map has capacity.
     *
     * @return true if an idle holder exists for an eligible map after this call
     */
    public boolean ensureEligibleGameHoldersLoaded(@Nullable GameMode category) {
        for (GameHolder holder : games) {
            if (holder.getGame() != null) {
                continue;
            }
            if (category != null && !holder.getMap().getGameModes().contains(category)) {
                continue;
            }
            return true;
        }
        for (GameMap map : GameMap.VALUES) {
            if (map == GameMap.MAIN_LOBBY || map == GameMap.MAIN_LOBBY_WHACK_A_MOLE) {
                continue;
            }
            if (category != null && !map.getGameModes().contains(category)) {
                continue;
            }
            if (ensureGameHoldersLoaded(map)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasJoinableGame(@Nullable GameMap map, @Nullable GameMode category) {
        for (GameHolder holder : games) {
            if (map != null && holder.getMap() != map) {
                continue;
            }
            if (category != null && !holder.getMap().getGameModes().contains(category)) {
                continue;
            }
            Game game = holder.getGame();
            if (game != null && game.acceptsPeople()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loads a single world from disk if unloaded and registers a game holder if missing.
     *
     * @return true if a holder exists for the world and map after this call
     */
    public boolean ensureGameHolderLoaded(@Nonnull String mapName, @Nonnull GameMap map) {
        if (hasGameHolder(mapName, map)) {
            return true;
        }
        World world = loadWorldIfPresent(mapName);
        if (world == null) {
            ChatUtils.MessageType.GAME.sendErrorMessage("Could not find game world " + mapName);
            return false;
        }
        if (hasGameHolder(mapName, map)) {
            return true;
        }
        addGameHolder(mapName, map, world);
        return true;
    }

    /**
     * Returns the world if already loaded, otherwise loads it via {@link WorldCreator}
     * when {@code level.dat} exists under the server world container. Does not generate
     * a new empty world when the folder is missing.
     * <p>
     * Returns {@code null} if worlds are currently being ticked, or if the session lock is
     * still held by this JVM (typically after a failed unload). Does not delete {@code session.lock}.
     */
    @Nullable
    private static World loadWorldIfPresent(@Nonnull String name) {
        World world = Bukkit.getWorld(name);
        if (world != null) {
            return world;
        }
        File levelDat = new File(Bukkit.getWorldContainer(), name + File.separator + "level.dat");
        if (!levelDat.isFile()) {
            return null;
        }
        if (Bukkit.isTickingWorlds()) {
            ChatUtils.MessageType.GAME.sendErrorMessage(
                    "Cannot load world " + name + " while worlds are being ticked."
            );
            return null;
        }
        ChatUtils.MessageType.GAME.sendMessage("Map " + name + " is unloaded. Loading it now.");
        try {
            return Bukkit.createWorld(new WorldCreator(name));
        } catch (RuntimeException e) {
            if (isWorldSessionLockFailure(e)) {
                ChatUtils.MessageType.GAME.sendErrorMessage(
                        "World " + name + " appears still locked by this server (failed unload or lock leak). "
                                + "Skipping load; a full server restart may be required."
                );
                ChatUtils.MessageType.GAME.sendErrorMessage(e);
                return null;
            }
            throw e;
        }
    }

    /**
     * True when {@code createWorld} failed because this JVM still holds the world's session lock.
     */
    private static boolean isWorldSessionLockFailure(@Nonnull Throwable throwable) {
        for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
            if (cause instanceof OverlappingFileLockException) {
                return true;
            }
            String message = cause.getMessage();
            if (message != null && (message.contains("already locked") || message.contains("session.lock"))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasGameHolder(@Nonnull String name, @Nonnull GameMap map) {
        return games.stream().anyMatch(holder -> holder.getName().equals(name) && holder.getMap() == map);
    }

    public enum QueueResult {
        READY_JOIN("You have joined an existing game."),
        READY_NEW("A new game has been made for you."),
        ERROR_FIND_GAME("We were unable to create a new game for you because of an internal error. Please report this."),
        ERROR_NEW_GAME("We were unable to find a new for you because of an internal error. Please report this."),
        ERROR_NO_WORLD_CAPACITY("All world instances for this map are currently in use."),
        EXPIRED("No game found in time"),
        CANCELLED("Cancelled queueing"),
        REPLACED("Replaced with another queue entry"),
        INVALID_GENERIC("Your request to queue was invalid because of an unknown reason. Please report this."),
        INVALID_OVERSIZE("Your request to queue was invalid because your party was too big for the specified map/game."),
        INVALID_MAP_CATEGORY("Your request to queue was invalid because the combination of map/category was not found."),
        CLOSE("The queue has been closed"),
        ;
        private final String message;

        QueueResult(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    public static class GameHolder {

        @Nonnull
        private final GameMap map;
        @Nonnull
        private final LocationFactory locations;
        @Nonnull
        private final String name;
        @Nullable
        private Game game;

        public GameHolder(@Nonnull GameMap map, @Nonnull LocationFactory locations, @Nonnull String name) {
            this.map = map;
            this.locations = locations;
            this.name = name;
        }

        @Nonnull
        public GameMap getMap() {
            return map;
        }

        @Nullable
        public Game getGame() {
            return game;
        }

        public void setGame(@Nullable Game game) {
            this.game = game;
        }

        public void forceEndGame() {
            if (game != null) {
                game.close();
                game = null;
            }
        }

        @Nonnull
        private Game optionallyStartNewGame(@Nonnull EnumSet<GameAddon> requestedGameAddons, @Nullable GameMode category) {
            Warlords.getGameManager().cancelIdleWorldUnload(name);
            if (game == null) {
                GameMode newCategory = category != null ? category
                                                        : map.getGameModes().get((int) (Math.random() * map.getGameModes().size()));
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

        @Nonnull
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
        private final int priority;
        private final int insertionId;
        @Nullable
        private BiConsumer<QueueResult, Game> onResult;

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

        public @org.jetbrains.annotations.Nullable GameMode getCategory() {
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

        public void setOnResult(@org.jetbrains.annotations.Nullable BiConsumer<QueueResult, Game> onResult) {
            this.onResult = onResult;
        }

        public void onResult(@Nonnull QueueResult res, @Nullable Game game) {
            if (onResult != null) {
                onResult.accept(res, game);
            }
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

    public class QueueEntryBuilder {

        @Nonnull
        protected List<OfflinePlayer> players;
        @Nonnull
        protected EnumSet<GameAddon> requestedGameAddons = EnumSet.noneOf(GameAddon.class);
        @Nullable
        protected GameMode gameMode = null;
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

        @Nonnull
        public List<OfflinePlayer> getPlayers() {
            return players;
        }

        public QueueEntryBuilder setPlayers(@Nonnull Collection<? extends OfflinePlayer> players) {
            this.players = new ArrayList<>(players);
            return this;
        }

        @Nonnull
        public EnumSet<GameAddon> getRequestedGameAddons() {
            return requestedGameAddons;
        }

        public QueueEntryBuilder setRequestedGameAddons(@Nonnull EnumSet<GameAddon> requestedGameAddons) {
            this.requestedGameAddons = requestedGameAddons.clone();
            return this;
        }

        public QueueEntryBuilder setRequestedGameAddons(@Nonnull GameAddon... rga) {
            return setRequestedGameAddons(rga.length == 0 ? EnumSet.noneOf(GameAddon.class) : EnumSet.copyOf(Arrays.asList(rga)));
        }

        @Nullable
        public GameMode getGameMode() {
            return gameMode;
        }

        public QueueEntryBuilder setGameMode(@Nullable GameMode category) {
            this.gameMode = category;
            return this;
        }

        @Nullable
        public GameMap getMap() {
            return map;
        }

        public QueueEntryBuilder setMap(@Nullable GameMap map) {
            this.map = map;
            return this;
        }

        public int getPriority() {
            return priority;
        }

        public QueueEntryBuilder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public @org.jetbrains.annotations.Nullable BiConsumer<QueueResult, Game> getOnResult() {
            return onResult;
        }

        public QueueEntryBuilder setOnResult(@Nonnull BiConsumer<QueueResult, Game> onResult) {
            this.onResult = onResult;
            return this;
        }

        public long getExpiresTime() {
            return expiresTime;
        }

        public QueueEntryBuilder setExpiresTime(long expiresTime) {
            this.expiresTime = expiresTime;
            return this;
        }

        public void queue() {
            GameManager.this.queue(new GameManager.QueueEntry(players, expiresTime, requestedGameAddons, gameMode, map, onResult, priority));
        }

        @Nonnull
        public Pair<QueueResult, Game> queueNow() {
            return GameManager.this.queueNow(new GameManager.QueueEntry(players, Long.MIN_VALUE, requestedGameAddons, gameMode, map, onResult, priority));
        }

    }
}
