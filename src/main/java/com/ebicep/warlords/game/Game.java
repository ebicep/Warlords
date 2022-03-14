package com.ebicep.warlords.game;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.AbstractWarlordsGameEvent;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.events.WarlordsGameUpdatedEvent;
import com.ebicep.warlords.events.WarlordsPointsChangedEvent;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.GameMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.state.ClosedState;
import com.ebicep.warlords.game.state.State;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An instance of an Warlords game. It depends on a state for its behavior. You
 * can also attach GameAddons to modify its global behavior, and attach options
 * to add specific small things.
 *
 * @see State
 * @see GameAddon
 * @see Option
 */
public final class Game implements Runnable, AutoCloseable {

    public static final String KEY_UPDATED_FROZEN = "frozen";

    private final UUID gameId = UUID.randomUUID();

    private final Map<UUID, Team> players = new HashMap<>();
    private final long createdAt = System.currentTimeMillis();
    private final List<BukkitTask> gameTasks = new ArrayList<>();
    private final List<Listener> eventHandlers = new ArrayList<>();
    private final EnumMap<Team, Integer> points = new EnumMap(Team.class);

    @Nonnull
    private final GameMap map;
    @Nonnull
    private final GameMode gameMode;
    @Nonnull
    private final EnumSet<GameAddon> addons;
    @Nonnull
    private List<Option> options;

    @Nullable
    private State state = null;
    @Nullable
    private State nextState = null;
    private boolean closed = false;
    private final List<String> frozenCauses = new CopyOnWriteArrayList<>();
    private int maxPlayers;
    private int minPlayers;
    private boolean acceptsPlayers;
    private boolean acceptsSpectators;
    private final LocationFactory locations;

    public Game(EnumSet<GameAddon> gameAddons, GameMap map, GameMode gameMode, LocationFactory locations) {
        this(gameAddons, map, gameMode, locations, map.initMap(gameMode, locations, gameAddons));
    }

    Game(EnumSet<GameAddon> gameAddons, GameMap map, GameMode gameMode, LocationFactory locations, List<Option> options) {
        this.locations = locations;
        this.addons = gameAddons;
        this.map = map;
        this.gameMode = gameMode;
        this.options = new ArrayList<>(options);
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
        this.options = Collections.unmodifiableList(options);
        state = this.map.initialState(this);
        for (Option option : options) {
            option.register(this);
        }
        for (Team team : TeamMarker.getTeams(this)) {
            this.points.put(team, 0);
        }
        state.begin();
        for (GameAddon addon : addons) {
            addon.stateHasChanged(this, null, state);
        }
        new GameRunnable(this) {
            @Override
            public void run() {
                Game.this.run();
            }
        }.runTaskTimer(0, 1);
        //this.printDebuggingInformation();
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
     * {@link GameMap#getCategories()} () getCategory} method
     * on the {@link #getMap() getMap method}
     *
     * @return the map category
     */
    @Nonnull
    public GameMode getGameMode() {
        return gameMode;
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
        return !frozenCauses.isEmpty();
    }

    @Nonnull
    public List<String> getFrozenCauses() {
        return Collections.unmodifiableList(frozenCauses);
    }

    public void addFrozenCause(String cause) {
        frozenCauses.add(cause);
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
    }

    public void removeFrozenCause(String cause) {
        frozenCauses.remove(cause);
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
    }

    public void clearFrozenCause() {
        frozenCauses.clear();
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
    }

    /**
     * An unique id assigned to this game.
     *
     * @return The UUID belonging to this game
     */
    public UUID getGameId() {
        return gameId;
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
     * @return true if it has closed
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
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        this.acceptsPlayers = acceptsPlayers;
    }

    public boolean acceptsSpectators() {
        return acceptsSpectators;
    }

    public void setAcceptsSpectators(boolean acceptsSpectators) {
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        this.acceptsSpectators = acceptsSpectators;
    }

    public void setNextState(@Nullable State nextState) {
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
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
     * @see #isOnTeam(java.util.UUID, Team)
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
     * @see #isOnTeam(java.util.UUID, Team)
     * @deprecated Use
     * <code>isOnTeam(java.util.UUID, com.ebicep.warlords.maps.Team)</code>
     * instead
     */
    @Deprecated
    public boolean isBlueTeam(@Nonnull UUID player) {
        return isOnTeam(player, Team.BLUE);
    }

    @Nullable
    public Team getPlayerTeam(@Nonnull UUID player) {
        return this.players.get(player);
    }

    @Nonnull
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
        /*
        if (!asSpectator && !this.acceptsPlayers) {
            throw new IllegalStateException("This game does not accepts player at the moment");
        }
        if (asSpectator && !this.acceptsSpectators) {
            throw new IllegalStateException("This game does not accepts spectators at the moment");
        }
         */
        this.players.put(player.getUniqueId(), null);
        this.state.onPlayerJoinGame(player, asSpectator);
        Player p = player.getPlayer();
        if (p != null) {
            this.state.onPlayerReJoinGame(p);
            Warlords.getInstance().hideAndUnhidePeople(p);
        }
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
            assert this.state != null : "A player was added and removed from the game while it was not started??";
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

    /**
     * Removes a player by UUID from the game
     *
     * @param player The player to remove
     * @return true if the player was part of the game before removing started
     */
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

    public List<UUID> removeAllPlayers() {
        List<UUID> toRemove = new ArrayList<>(this.players.keySet());
        for (UUID p : toRemove) {
            this.removePlayer0(p);
        }
        if (!toRemove.isEmpty()) {
            Warlords.getInstance().hideAndUnhidePeople();
        }
        assert this.players.isEmpty();
        return toRemove;
    }

    public boolean hasPlayer(UUID player) {
        return this.players.containsKey(player);
    }

    public int playersCount() {
        return (int) this.players.values().stream().filter(Objects::nonNull).count();
    }

    public int spectatorsCount() {
        return (int) this.players.values().stream().filter(Objects::isNull).count();
    }

    public Stream<WarlordsPlayer> warlordsPlayers() {
        return this.players.entrySet().stream().map(e -> Warlords.getPlayer(e.getKey())).filter(Objects::nonNull);
    }

    public Stream<Map.Entry<UUID, Team>> players() {
        return this.players.entrySet().stream();
    }

    public Stream<Map.Entry<UUID, Team>> playersWithoutSpectators() {
        return this.players.entrySet().stream().filter(e -> e.getValue() != null);
    }

    public Stream<Map.Entry<OfflinePlayer, Team>> offlinePlayersWithoutSpectators() {
        return playersWithoutSpectators()
                .map(e -> new AbstractMap.SimpleImmutableEntry<>(
                Bukkit.getOfflinePlayer(e.getKey()),
                e.getValue()
        ));
    }

    public Stream<Map.Entry<Player, Team>> onlinePlayersWithoutSpectators() {
        return playersWithoutSpectators()
                .<Map.Entry<Player, Team>>map(e -> new AbstractMap.SimpleImmutableEntry<>(
                Bukkit.getPlayer(e.getKey()),
                e.getValue()
        )).filter(e -> e.getKey() != null);
    }

    public Stream<Map.Entry<Player, Team>> onlinePlayers() {
        return players()
                .<Map.Entry<Player, Team>>map(e -> new AbstractMap.SimpleImmutableEntry<>(
                Bukkit.getPlayer(e.getKey()),
                e.getValue()
        )).filter(e -> e.getKey() != null);
    }

    public Stream<UUID> spectators() {
        return this.players.entrySet().stream().filter(e -> e.getValue() == null).map(e -> e.getKey());
    }

    public void forEachOfflinePlayer(BiConsumer<OfflinePlayer, Team> consumer) {
        offlinePlayersWithoutSpectators().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachOfflineWarlordsPlayer(Consumer<WarlordsPlayer> consumer) {
        warlordsPlayers().forEach(consumer);
    }

    public void forEachOnlinePlayer(BiConsumer<Player, Team> consumer) {
        onlinePlayers().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachOnlinePlayerWithoutSpectators(BiConsumer<Player, Team> consumer) {
        onlinePlayersWithoutSpectators().forEach(entry -> consumer.accept(entry.getKey(), entry.getValue()));
    }

    public void forEachOnlineWarlordsPlayer(Consumer<WarlordsPlayer> consumer) {
        warlordsPlayers().filter(WarlordsPlayer::isOnline).forEach(consumer);
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
            nextState = null;
            //System.out.println("DEBUG OLD TO NEW STATE");
            //this.printDebuggingInformation();
            State oldState = this.state;
            this.state = newState;
            newState.begin();
            for (GameAddon addon : this.addons) {
                addon.stateHasChanged(this, oldState, newState);
            }
        }
    }

    @Nonnull
    @Deprecated
    public List<ScoreboardHandler> getScoreboardHandlers() {
        return this.getMarkers(ScoreboardHandler.class);
    }

    @Deprecated
    public void registerScoreboardHandler(@Nonnull ScoreboardHandler handler) {
        this.registerGameMarker(ScoreboardHandler.class, handler);
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
            throw new IllegalArgumentException("Attempted to register a marker for interface " + clazz.getName() + " while passing class " + object.getClass().getName() + " does not implement this");
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
     * Registers a Bukkit task to be cancelled once the game ends.Cancelling is
     * important for proper cleanup
     *
     * @param task The task to register
     * @return The task itself
     * @throws IllegalStateException when the game has been closed (this also
     * directly calls the cancel function)
     */
    @Nonnull
    public BukkitTask registerGameTask(@Nonnull BukkitTask task) {
        if (this.closed) {
            task.cancel();
            throw new IllegalStateException("Game has been closed");
        }
        this.gameTasks.add(Objects.requireNonNull(task, "task"));
        return task;
    }

    public BukkitTask registerGameTask(Runnable task) {
        return this.registerGameTask(Bukkit.getScheduler().runTask(Warlords.getInstance(), task));
    }

    public BukkitTask registerGameTask(Runnable task, int delay) {
        return this.registerGameTask(Bukkit.getScheduler().runTaskLater(Warlords.getInstance(), task, delay));
    }

    public BukkitTask registerGameTask(Runnable task, int delay, int period) {
        return this.registerGameTask(Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), task, delay, period));
    }

    // Modified version of {@link org.bukkit.plugin.SimplePluginManager#getEventListeners}
    private HandlerList getEventListeners(Class<? extends Event> type) {
        try {
            Method method = getRegistrationClass(type).getDeclaredMethod("getHandlerList");
            method.setAccessible(true);
            return (HandlerList) method.invoke(null, new Object[0]);
        } catch (Exception e) {
            throw new IllegalPluginAccessException(e.toString());
        }
    }

    // Modified version of {@link org.bukkit.plugin.SimplePluginManager#getRegistrationClass}
    private Class<? extends Event> getRegistrationClass(Class<? extends Event> clazz) {
        try {
            clazz.getDeclaredMethod("getHandlerList");
            return clazz;
        } catch (NoSuchMethodException localNoSuchMethodException) {
            if ((clazz.getSuperclass() != null)
                    && (!clazz.getSuperclass().equals(Event.class))
                    && (Event.class.isAssignableFrom(clazz.getSuperclass()))) {
                return getRegistrationClass(clazz.getSuperclass().asSubclass(Event.class));
            }
        }
        throw new IllegalPluginAccessException("Unable to find handler list for event " + clazz.getName() + ". Static getHandlerList method required!");
    }

    /**
     * Registers a event listener
     *
     * @param listener The event listener to register
     */
    public void registerEvents(Listener listener) {
        if (this.state == null) {
            throw new IllegalStateException("The game is not started yet");
        }
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        this.eventHandlers.add(listener);
        // Manually register events here, this way we can add support for
        // filtering the WarlordsGameEvent to be from this game instance only.
        // This makes the implementing of other code comsuming these events
        // overall simpler.
        // See the source code of {@link org.bukkit.plugin.SimplePluginManager#registerEvents}
        if (!Warlords.getInstance().isEnabled()) {
            throw new IllegalPluginAccessException("Plugin attempted to register " + listener + " while not enabled");
        }
        for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> entry : Warlords.getInstance().getPluginLoader().createRegisteredListeners(listener, Warlords.getInstance()).entrySet()) {
            if (AbstractWarlordsGameEvent.class.isAssignableFrom(entry.getKey())) {
                entry.setValue(entry.getValue().stream().map(rl -> {
                    return new RegisteredListener(rl.getListener(), (l, e) -> {
                        AbstractWarlordsGameEvent wge = (AbstractWarlordsGameEvent) e;
                        if (wge.getGame() == Game.this) {
                            rl.callEvent(e);
                        }
                    }, rl.getPriority(), rl.getPlugin(), false);
                }).collect(Collectors.toSet()));
            }
            getEventListeners(getRegistrationClass(entry.getKey())).registerAll(entry.getValue());
        }
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        List<Throwable> exceptions = new ArrayList<>();
        for (BukkitTask task : gameTasks) {
            task.cancel();
        }
        gameTasks.clear();
        for (Listener listener : eventHandlers) {
            HandlerList.unregisterAll(listener);
        }
        eventHandlers.clear();
        try {
            removeAllPlayers();
        } catch (Throwable e) {
            exceptions.add(e);
        }
        for (Option option : options) {
            try {
                option.onGameCleanup(this);
            } catch (Throwable e) {
                exceptions.add(e);
            }
        }
        this.acceptsPlayers = false;
        this.acceptsSpectators = false;
        this.nextState = null;
        if (this.state != null && !(this.state instanceof ClosedState)) {
            try {
                this.state.end();
            } catch (Throwable e) {
                exceptions.add(e);
            }
            this.state = new ClosedState(this);
        }
        this.options = Collections.emptyList();
        if (!exceptions.isEmpty()) {
            RuntimeException e = new RuntimeException("Problems closing the game", exceptions.get(0));
            for (int i = 1; i < exceptions.size(); i++) {
                e.addSuppressed(exceptions.get(i));
            }
            throw e;
        }
    }

    @Override
    public String toString() {
        return "Game{"
                + "\nplayers=" + players.entrySet().stream().map(Object::toString).collect(Collectors.joining("\n\t", "\n\t", ""))
                + ",\ncreatedAt=" + createdAt
                + ",\ngameTasks=" + gameTasks
                + ",\neventHandlers=" + eventHandlers
                + ",\nmap=" + map
                + ",\ncategory=" + gameMode
                + ",\naddons=" + addons
                + ",\noptions=" + options
                + ",\nstate=" + state
                + ",\nnextState=" + nextState
                + ",\nclosed=" + closed
                + ",\nfrozenCauses=" + frozenCauses
                + ",\nmaxPlayers=" + maxPlayers
                + ",\nminPlayers=" + minPlayers
                + ",\nacceptsPlayers=" + acceptsPlayers
                + ",\nacceptsSpectators=" + acceptsSpectators
                + ",\ngameMarkers=" + gameMarkers
                        .entrySet()
                        .stream()
                        .map(e -> e.getKey().getSimpleName()+ ": " + e.getValue().stream().map(Object::toString).collect(Collectors.joining("\n\t\t", "\n\t\t", "")))
                        .collect(Collectors.joining("\n\t", "\n\t", ""))
                + ",\nlocations=" + locations
                + "\n}";
    }

    @Deprecated
    public void printDebuggingInformation() {
        Warlords.getInstance().getLogger().info(String.valueOf(this));
    }

    public int getPoints(@Nonnull Team team) {
        Integer oldPointsObj = this.points.get(team);
        if (oldPointsObj == null) {
            throw new IllegalArgumentException("Team " + team + " is not part of this game");
        }
        return oldPointsObj;
    }

    public void addPoints(@Nonnull Team team, int addPoints) {
        Integer oldPointsObj = this.points.get(team);
        if (oldPointsObj == null) {
            throw new IllegalArgumentException("Team " + team + " is not part of this game");
        }
        int oldPoints = oldPointsObj;
        int points = oldPoints + addPoints;
        this.points.put(team, points);
        Bukkit.getPluginManager().callEvent(new WarlordsPointsChangedEvent(Game.this, team, oldPoints, points));
    }

    public void setPoints(@Nonnull Team team, int points) {
        Integer oldPointsObj = this.points.get(team);
        if (oldPointsObj == null) {
            throw new IllegalArgumentException("Team " + team + " is not part of this game");
        }
        int oldPoints = oldPointsObj;
        this.points.put(team, points);
        Bukkit.getPluginManager().callEvent(new WarlordsPointsChangedEvent(Game.this, team, oldPoints, points));
    }
}
