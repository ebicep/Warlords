package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.events.WarlordsGameUpdatedEvent;
import com.ebicep.warlords.maps.option.Option;
import com.ebicep.warlords.maps.option.marker.GameMarker;
import com.ebicep.warlords.maps.option.techincal.GameFreezeOption;
import com.ebicep.warlords.maps.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.maps.state.*;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.LocationFactory;
import static com.ebicep.warlords.util.Utils.collectionHasItem;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * An instance of an Warlords game. It depends on a
 */
public final class Game implements Runnable, AutoCloseable {

    public static final String KEY_UPDATED_FROZEN = "frozen";

    private final Map<UUID, Team> players = new HashMap<>();
    private final long createdAt = System.currentTimeMillis();
    private final List<ScoreboardHandler> scoreboardHandlers = new CopyOnWriteArrayList<>();
    private final List<BukkitTask> gameTasks = new ArrayList<>();
    private final List<Listener> eventHandlers = new ArrayList<>();

    @Nonnull
    private final GameMap map;
    @Nonnull
    private final MapCategory category;
    @Nonnull
    private final EnumSet<GameAddon> addons;
    @Nonnull
    private final List<Option> options;

    @Nullable
    private State state = null;
    @Nullable
    private State nextState = null;
    private boolean closed = false;
    private final List<String> frozenCauses = new CopyOnWriteArrayList<>();
    private volatile boolean frozenCached = false;
    private int maxPlayers;
    private int minPlayers;
    private boolean acceptsPlayers;
    private boolean acceptsSpectators;
    private final LocationFactory locations;

    public Game(EnumSet<GameAddon> gameAddons, GameMap map, MapCategory category, LocationFactory locations) {
        this.locations = locations;
        this.addons = gameAddons;
        this.map = map;
        this.category = category;
        this.options = new ArrayList<>(map.initMap(MapCategory.OTHER, locations, gameAddons));
        if (!collectionHasItem(options, e -> e instanceof GameFreezeOption)) {
            options.add(new GameFreezeOption());
        }
        this.minPlayers = map.getMinPlayers();
        this.maxPlayers = map.getMaxPlayers();
        for (GameAddon addon : gameAddons) {
            this.maxPlayers = addon.getMaxPlayers(map, this.maxPlayers);
        }
    }

    public void start() {
        if (state != null) {
            throw new IllegalStateException("Game already started");
        }
        for (GameAddon addon : addons) {
            addon.modifyGame(this);
        }
        state = this.map.initialState(this);
        state.begin();
    }

    public boolean isState(Class<? extends State> clazz) {
        if (this.state == null) {
            throw new IllegalStateException("The game is not started yet");
        }
        return clazz.isAssignableFrom(this.state.getClass());
    }

    public <T extends State> Optional<T> getState(Class<T> clazz) {
        if (this.state == null) {
            throw new IllegalStateException("The game is not started yet");
        }
        if (clazz.isAssignableFrom(this.state.getClass())) {
            return Optional.of((T) this.state);
        }
        return Optional.empty();
    }

    @Nonnull
    public State getState() {
        if (this.state == null) {
            throw new IllegalStateException("The game is not started yet");
        }
        return state;
    }

    /**
     * Gets the game map used to construct this game
     *
     * @return the game map
     */
    @Nonnull
    public GameMap getMap() {
        return map;
    }

    /**
     * Gets the used map category for construction. This is any of the
     * categories returned by
     * {@link com.ebicep.warlords.maps.GameMap#getCategory() getCategory} method
     * on the {@link #getMap() getMap method}
     *
     * @return the map category
     */
    @Nonnull
    public MapCategory getCategory() {
        return category;
    }

    public LocationFactory getLocations() {
        return locations;
    }

    /**
     * Check if the game is frozen
     *
     * @return true if the game is frozen
     */
    public boolean isFrozen() {
        return frozenCached;
    }

    @Nonnull
    public List<String> getFrozenCauses() {
        return Collections.unmodifiableList(frozenCauses);
    }

    public void addFrozenCause(String cause) {
        frozenCauses.add(cause);
        boolean oldFrozenCached = frozenCached;
        frozenCached = true;
        if (oldFrozenCached != frozenCached) {
            Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
        }
    }

    public void removeFrozenCause(String cause) {
        frozenCauses.remove(cause);
        boolean oldFrozenCached = frozenCached;
        frozenCached = !frozenCauses.isEmpty();
        if (oldFrozenCached != frozenCached) {
            Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
        }
    }

    public void clearFrozenCause() {
        frozenCauses.clear();
        boolean oldFrozenCached = frozenCached;
        frozenCached = false;
        if (oldFrozenCached != frozenCached) {
            Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
        }
    }

    @Nonnull
    public EnumSet<GameAddon> getAddons() {
        return addons;
    }

    @Nonnull
    public List<Option> getOptions() {
        return options;
    }

    /**
     * Returns the creation time of this game instance
     *
     * @return the creation time
     */
    public long createdAt() {
        return createdAt;
    }

    /**
     * Checks if this game has been marked closed. A closed game does not accept
     * any gametasks or players, and can no longer be used
     *
     * @return
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Get the maximum amount of internalPlayers supported by this game
     *
     * @return The maximum
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public boolean acceptsPeople() {
        return acceptsPlayers;
    }

    public void setAcceptsPlayers(boolean acceptsPlayers) {
        this.acceptsPlayers = acceptsPlayers;
    }

    public boolean acceptsSpectators() {
        return acceptsSpectators;
    }

    public void setAcceptsSpectators(boolean acceptsSpectators) {
        this.acceptsSpectators = acceptsSpectators;
    }

    public void setNextState(@Nullable State nextState) {
        this.nextState = nextState;
    }

    /**
     * Checks if a player is on a team, spectators are said to be on team
     * <code>null</code>
     *
     * @param player The player to check
     * @param team The team to check, of null for spectators
     * @return True if the player is in the specified team
     */
    public boolean isOnTeam(@Nonnull UUID player, @Nullable Team team) {
        return players.containsKey(player) && players.get(player) == team;
    }

    /**
     * @param player
     * @return
     * @see #isOnTeam(java.util.UUID, com.ebicep.warlords.maps.Team)
     * @deprecated Use
     * <code>isOnTeam(java.util.UUID, com.ebicep.warlords.maps.Team)</code>
     * instead
     */
    @Deprecated
    public boolean isRedTeam(@Nonnull UUID player) {
        return isOnTeam(player, Team.RED);
    }

    /**
     * @param player
     * @return
     * @see #isOnTeam(java.util.UUID, com.ebicep.warlords.maps.Team)
     * @deprecated Use
     * <code>isOnTeam(java.util.UUID, com.ebicep.warlords.maps.Team)</code>
     * instead
     */
    @Deprecated
    public boolean isBlueTeam(@Nonnull UUID player) {
        return isOnTeam(player, Team.BLUE);
    }

    @Nullable
    public Team getPlayerTeamOrNull(@Nonnull UUID player) {
        return this.players.get(player);
    }

    public Map<UUID, Team> getPlayers() {
        return players;
    }

    /**
     * Adds a player into the game
     *
     * @param player The player to add
     * @param asSpectator If the player should be added as an spectator
     * @see #acceptsPeople()
     * @see #acceptsSpectators()
     */
    public void addPlayer(@Nonnull OfflinePlayer player, boolean asSpectator) {
        Validate.notNull(player, "player");
        if (this.state == null) {
            throw new IllegalStateException("The game is not started yet");
        }
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        if (!asSpectator && !this.acceptsPlayers) {
            throw new IllegalStateException("This game does not accepts player at the moment");
        }
        if (asSpectator && !this.acceptsSpectators) {
            throw new IllegalStateException("This game does not accepts spectators at the moment");
        }
        this.players.put(player.getUniqueId(), null);
        this.state.onPlayerJoinGame(player, asSpectator);
    }

    public void setPlayerTeam(@Nonnull OfflinePlayer player, @Nonnull Team team) {
        Validate.notNull(player, "player");
        Validate.notNull(team, "team");
        if (!this.players.containsKey(player.getUniqueId())) {
            throw new IllegalArgumentException("The specified player is not part of this game");
        }
        Team oldTeam = this.players.get(player.getUniqueId());
        if (team == oldTeam) {
            return;
        }
        this.players.put(player.getUniqueId(), team);
    }

    private boolean removePlayer0(UUID player) {
        if (this.players.containsKey(player)) {
            assert this.state != null;
            OfflinePlayer op = Bukkit.getOfflinePlayer(player);
            this.state.onPlayerQuitGame(op);
            this.players.remove(player);
            Warlords.removePlayer(player);
            Player p = op.getPlayer();
            if (p != null) {
                WarlordsEvents.joinInteraction(p, true);
            }
            return true;
        }
        return false;
    }
    public boolean removePlayer(UUID player) {
        if (removePlayer0(player)) {
            Player p = Bukkit.getPlayer(player);
            if (p != null) {
                Warlords.getInstance().hideAndUnhidePeople(p);
            }
            return true;
        }
        return false;
    }

    public List<UUID> clearAllPlayers() {
        try {
            //making hidden internalPlayers visible again
            Warlords.getPlayers().forEach(((uuid, warlordsPlayer) -> {
                if (warlordsPlayer.getEntity() instanceof Player) {
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                        ((Player) warlordsPlayer.getEntity()).showPlayer(onlinePlayer);
                    });
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<UUID> toRemove = new ArrayList<>(this.players.keySet());
        for (UUID p : toRemove) {
            this.removePlayer0(p);
        }
        Warlords.getInstance().hideAndUnhidePeople();
        assert this.players.isEmpty();
        return toRemove;
    }

    public int playersCount() {
        return this.players.size();
    }

    private Stream<Map.Entry<UUID, Team>> internalPlayers() {
        return this.players.entrySet().stream();
    }

    public Stream<WarlordsPlayer> warlordsPlayers() {
        return this.players.entrySet().stream().map(e -> Warlords.getPlayer(e.getKey())).filter(Objects::nonNull);
    }

    public Stream<Map.Entry<OfflinePlayer, Team>> offlinePlayers() {
        return this.players.entrySet()
                .stream()
                .map(e -> new AbstractMap.SimpleImmutableEntry<>(
                Bukkit.getOfflinePlayer(e.getKey()),
                e.getValue()
        ));
    }

    public Stream<Map.Entry<Player, Team>> onlinePlayers() {
        return this.players.entrySet()
                .stream()
                .<Map.Entry<Player, Team>>map(e -> new AbstractMap.SimpleImmutableEntry<>(
                Bukkit.getPlayer(e.getKey()),
                e.getValue()
        ))
                .filter(e -> e.getKey() != null);
    }

    public void forEachOfflinePlayer(BiConsumer<OfflinePlayer, Team> consumer) {
        offlinePlayers().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachOfflineWarlordsPlayer(Consumer<WarlordsPlayer> consumer) {
        offlinePlayers().map(w -> Warlords.getPlayer(w.getKey())).filter(Objects::nonNull).forEach(consumer);
    }

    public void forEachOnlinePlayer(BiConsumer<Player, Team> consumer) {
        onlinePlayers().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachOnlineWarlordsPlayer(Consumer<WarlordsPlayer> consumer) {
        onlinePlayers().map(w -> Warlords.getPlayer(w.getKey())).filter(Objects::nonNull).forEach(consumer);
    }

    public Stream<UUID> getSpectators() {
        return internalPlayers().filter(e -> e.getValue() == null).map(e -> e.getKey());
    }

    @Override
    public void run() {
        if (this.nextState == null && this.state != null) {
            this.nextState = this.state.run();
        }
        while (this.nextState != null) {
            for (GameAddon addon : this.addons) {
                nextState = addon.stateWillChange(this, this.state, nextState);
                if (nextState == null) {
                    return;
                }
            }
            if (this.state != null) {
                this.state.end();
            }
            State newState = nextState == null ? new ClosedState(this) : nextState;
            System.out.println("DEBUG OLD TO NEW STATE");
            Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "old: " + this.state + " --> new: " + newState);
            State oldState = this.state;
            this.state = newState;
            this.state.begin();
            for(GameAddon addon : this.addons) {
                addon.stateHasChanged(this, oldState, newState);
            }
        }
    }

    @Nonnull
    public List<ScoreboardHandler> getScoreboardHandlers() {
        return scoreboardHandlers;
    }

    public void registerScoreboardHandler(@Nonnull ScoreboardHandler handler) {
        if (this.state == null) {
            throw new IllegalStateException("The game is not started yet");
        }
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        scoreboardHandlers.add(handler);
    }
    private final Map<Class<? extends GameMarker>, List<GameMarker>> gameMarkers = new HashMap<>();

    /**
     * Registers a gamemarker.
     *
     * @param <T> The type of gamemarker to register
     * @param clazz The clazz of the type
     * @param object The actual object to register
     * @throws IllegalStateException when the game has been closed
     */
    public <T extends GameMarker> void registerGameMarker(@Nonnull Class<T> clazz, @Nonnull T object) {
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        if (!clazz.isAssignableFrom(object.getClass())) {
            throw new IllegalArgumentException("Attempted to register a marker for interface " + clazz.getName() + " while passed class " + object.getClass().getName() + " does not implement this");
        }
        gameMarkers.computeIfAbsent(clazz, e -> new ArrayList<>()).add(object);
    }

    /**
     * Gets the list of all registered game markers for a specified
     *
     * @param <T> The type to return
     * @param clazz A class instance of the requested marker
     * @return The requested list, or Collections.EMPTY_LIST if none is found
     */
    @Nonnull
    public <T extends GameMarker> List<T> getMarkers(@Nonnull Class<T> clazz) {
        return (List<T>) gameMarkers.getOrDefault(clazz, Collections.emptyList());
    }
    
    public void unregisterGameTask(@Nonnull BukkitTask task) {
        if (this.closed) {
            return;
        }
        this.gameTasks.remove(Objects.requireNonNull(task, "task"));
    }

    /**
     * Registers a Bukkit task to be cancelled once the game ends. Cancelling is important for proper cleanup
     * @param task The task to register
     * @throws IllegalStateException when the game has been closed (this also directly calls the cancel function)
     */
    public void registerGameTask(@Nonnull BukkitTask task) {
        if (this.closed) {
            task.cancel();
            throw new IllegalStateException("Game has been closed");
        }
        this.gameTasks.add(Objects.requireNonNull(task, "task"));
    }

    public void registerGameTask(Runnable task) {
        this.registerGameTask(Bukkit.getScheduler().runTask(Warlords.getInstance(), task));
    }

    public void registerGameTask(Runnable task, int delay) {
        this.registerGameTask(Bukkit.getScheduler().runTaskLater(Warlords.getInstance(), task, delay));
    }

    public void registerGameTask(Runnable task, int delay, int period) {
        this.registerGameTask(Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), task, delay, period));
    }

    /**
     * Registers a event listener
     *
     * @param events The event listener to register
     */
    public void registerEvents(Listener events) {
        if (this.state == null) {
            throw new IllegalStateException("The game is not started yet");
        }
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        this.eventHandlers.add(events);
        Bukkit.getPluginManager().registerEvents(events, Warlords.getInstance());
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        for (BukkitTask task : gameTasks) {
            task.cancel();
        }
        gameTasks.clear();
        for (Listener listener : eventHandlers) {
            HandlerList.unregisterAll(listener);
        }
        eventHandlers.clear();
        clearAllPlayers();
    }

    @Override
    public String toString() {
        return "Game{"
                + "\nplayers=" + players
                + ",\ncreatedAt=" + createdAt
                + ",\nscoreboardHandlers=" + scoreboardHandlers
                + ",\ngameTasks=" + gameTasks
                + ",\neventHandlers=" + eventHandlers
                + ",\nmap=" + map
                + ",\ncategory=" + category
                + ",\naddons=" + addons
                + ",\noptions=" + options
                + ",\nstate=" + state
                + ",\nnextState=" + nextState
                + ",\nclosed=" + closed
                + ",\nfrozenCauses=" + frozenCauses
                + ",\nfrozenCached=" + frozenCached
                + ",\nmaxPlayers=" + maxPlayers
                + ",\nminPlayers=" + minPlayers
                + ",\nacceptsPlayers=" + acceptsPlayers
                + ",\nacceptsSpectators=" + acceptsSpectators
                + ",\ngameMarkers=" + gameMarkers
                + ",\nlocations=" + locations
                + "\n}";
    }

    public void printDebuggingInformation() {
        System.out.println(this);
    }

}
