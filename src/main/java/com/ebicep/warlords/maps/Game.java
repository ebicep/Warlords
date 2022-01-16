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
import com.ebicep.warlords.util.PacketUtils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
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
    private boolean closed = false;
    private List<String> frozenCauses = new CopyOnWriteArrayList<>();
    private volatile boolean frozenCached = false;
    private int maxPlayers;
    private int minPlayers;
    private boolean acceptsPeople;

    private static void addBasicOptions(List<Option> options) {
        options.add(new GameFreezeOption());
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public Game(EnumSet<GameAddon> gameAddons, GameMap map, MapCategory category, LocationFactory locations) {
        this.addons = gameAddons;
        this.map = map;
        this.category = category;
        List<Option> options = new ArrayList<>(map.initMap(MapCategory.OTHER, locations, gameAddons));
        addBasicOptions(options);// This modifies options;
        this.options = options;
        this.minPlayers = map.getMinPlayers();
        this.maxPlayers = map.getMaxPlayers();
        for (GameAddon addon : gameAddons) {
            this.maxPlayers = addon.getMaxPlayers(this.maxPlayers);
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

    /**
     * Check if the game is frozen
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
        frozenCached = true;
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
    }
    
    public void removeFrozenCause(String cause) {
        frozenCauses.remove(cause);
        frozenCached = !frozenCauses.isEmpty();
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
    }
    
    public void clearFrozenCause() {
        frozenCauses.clear();
        frozenCached = false;
        Bukkit.getPluginManager().callEvent(new WarlordsGameUpdatedEvent(this, KEY_UPDATED_FROZEN));
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
     * Get the maximum amount of players supported by this game
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
        return acceptsPeople;
    }

    public void setAcceptsPeople(boolean acceptsPeople) {
        this.acceptsPeople = acceptsPeople;
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

    public void addPlayer(@Nonnull OfflinePlayer player) {
        Validate.notNull(player, "player");
        Player online = player.getPlayer();
        if (online != null) {
            online.setGameMode(GameMode.ADVENTURE);
            online.setAllowFlight(false);
        }
        this.players.put(player.getUniqueId(), team);
        Location loc = this.map.getLobbySpawnPoint(team);
        Warlords.setRejoinPoint(player.getUniqueId(), loc);
    }

    public void setPlayerTeam(@Nonnull OfflinePlayer player, @Nonnull Team team) {
        Validate.notNull(player, "player");
        Validate.notNull(team, "team");
        Player online = player.getPlayer();
        Team oldTeam = this.players.get(player.getUniqueId());
        if (team == oldTeam) {
            return;
        }
        Warlords.getPlayerSettings(player.getUniqueId()).setWantedTeam(team);
        this.players.put(player.getUniqueId(), team);
        Location loc = this.map.getLobbySpawnPoint(team);
        Warlords.setRejoinPoint(player.getUniqueId(), loc);
        if (online != null) {
            online.teleport(loc);
        }
    }

    public void removePlayer(UUID player) {
        this.players.remove(player);
        Warlords.removePlayer(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            WarlordsEvents.joinInteraction(p, true);
        }
    }

    public List<UUID> clearAllPlayers() {
        try {
            //making hidden players visible again
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
            this.removePlayer(p);
        }
        assert this.players.isEmpty();
        return toRemove;
    }

    public int playersCount() {
        return this.players.size();
    }

    public Stream<Map.Entry<UUID, Team>> players() {
        return this.players.entrySet().stream();
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

    public boolean onSameTeam(UUID player1, UUID player2) {
        return players.get(player1) == players.get(player2);
    }

    public boolean onSameTeam(Player player1, Player player2) {
        return onSameTeam(player1.getUniqueId(), player2.getUniqueId());
    }

    /**
     * See if players are on the same team
     *
     * @param player1 First player
     * @param player2 Second player
     * @return true is they are on the same team (eg BLUE && BLUE, RED && RED or
     * &lt;not player> && &lt;not playing>
     * @deprecated Use WarLordsPlayer.isTeammate instead
     */
    @Deprecated
    public boolean onSameTeam(@Nonnull WarlordsPlayer player1, @Nonnull WarlordsPlayer player2) {
        return onSameTeam(player1.getUuid(), player2.getUuid());
    }

    public Stream<UUID> getSpectators() {
        return players().filter(e -> e.getValue() == null).map(e -> e.getKey());
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
        Player player = Bukkit.getPlayer(uuid);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(this.getMap().getBlueRespawn());
        Warlords.setRejoinPoint(player.getUniqueId(), this.getMap().getBlueRespawn());
        if (state instanceof PreLobbyState) {
            ((PreLobbyState) state).giveLobbyScoreboard(true, player);
        } else if (state instanceof PlayingState) {
            ((PlayingState) state).updateBasedOnGameState(true, Warlords.playerScoreboards.get(player.getUniqueId()), null);
        }
    }

    public void removeSpectator(UUID uuid, boolean fromMenu) {
        if (fromMenu) {
            spectators.remove(uuid);
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        Location loc = Warlords.spawnPoints.remove(player.getUniqueId());
        if (player.isOnline()) {
            if (loc != null) {
                player.getPlayer().teleport(Warlords.getRejoinPoint(player.getUniqueId()));
            }
            WarlordsEvents.joinInteraction(player.getPlayer(), true);
        }

    }

    @Override
    public void run() {
        State newState = state.run();
        if (newState != null) {
            this.state.end();
            System.out.println("DEBUG OLD TO NEW STATE");
            Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "New State = " + newState + " / Old State = " + this.state);
            this.state = newState;
            newState.begin();
        }

    }

    @Nonnull
    public List<ScoreboardHandler> getScoreboardHandlers() {
        return scoreboardHandlers;
    }

    public void registerScoreboardHandler(@Nonnull ScoreboardHandler handler) {
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
     */
    public <T extends GameMarker> void registerGameMarker(@Nonnull Class<T> clazz, @Nonnull T object) {
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

    public void registerGameTask(BukkitTask task) {
        if (this.closed) {
            throw new IllegalStateException("Game has been closed");
        }
        this.gameTasks.add(task);
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
        for(Listener listener : eventHandlers) {
            HandlerList.unregisterAll(listener);
        }
        eventHandlers.clear();
    }

}
