package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.state.InitState;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.maps.state.PreLobbyState;
import com.ebicep.warlords.maps.state.State;
import com.ebicep.warlords.player.WarlordsPlayer;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Game implements Runnable {

    public static TextComponent spacer = new TextComponent(ChatColor.GRAY + " - ");
    private State state = null;
    private final Map<UUID, Team> players = new HashMap<>();
    private GameMap map = GameMap.RIFT;
    private boolean cooldownMode;
    private boolean gameFreeze = false;

    private List<OfflinePlayer> spectators = new ArrayList<>();

    public boolean isState(Class<? extends State> clazz) {
        return clazz.isAssignableFrom(this.state.getClass());
    }

    public <T extends State> Optional<T> getState(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.state.getClass())) {
            return Optional.of((T) this.state);
        }
        return Optional.empty();
    }

    public State getState() {
        return state;
    }

    public GameMap getMap() {
        return map;
    }

    public boolean isRedTeam(@Nonnull UUID player) {
        return players.get(player) == Team.RED;
    }

    public boolean isBlueTeam(@Nonnull UUID player) {
        return players.get(player) == Team.BLUE;
    }

    @Nullable
    public Team getPlayerTeamOrNull(@Nonnull UUID player) {
        return this.players.get(player);
    }

    public boolean getCooldownMode() {
        return this.cooldownMode;
    }

    public void setCooldownMode(boolean cooldownMode) {
        this.cooldownMode = cooldownMode;
    }

    @Nonnull
    public Team getPlayerTeam(@Nonnull UUID player) {
        Team team = getPlayerTeamOrNull(player);
        if (team == null) {
            throw new IllegalArgumentException("Player provided is not playing a game at the moment");
        }
        return team;
    }

    public boolean canChangeMap() {
        return players.isEmpty() && (state instanceof PreLobbyState || state instanceof InitState);
    }

    public void changeMap(@Nonnull GameMap map) {
        if (!canChangeMap()) {
            throw new IllegalStateException("Cannot change map!");
        }
        this.map = map;
        if (state != null) {
            state.end();
            state = null;
        }
    }

    public Map<UUID, Team> getPlayers() {
        return players;
    }

    public void addPlayer(@Nonnull OfflinePlayer player, @Nonnull Team team) {
        Validate.notNull(player, "player");
        Validate.notNull(team, "team");
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
        this.players.put(player.getUniqueId(), team);
        Location loc = this.map.getLobbySpawnPoint(team);
        Warlords.setRejoinPoint(player.getUniqueId(), loc);
        if (online != null) {
            online.teleport(loc);
        }
    }

    /**
     * Adds a player to the game
     *
     * @param player
     * @param teamBlue
     * @deprecated use {@link #addPlayer(Player, Team) addPlayer(Player, Team)} instead
     */
    @Deprecated
    public void addPlayer(Player player, boolean teamBlue) {
        if (teamBlue) {
            this.addPlayer(player, Team.BLUE);
        } else {
            this.addPlayer(player, Team.RED);
        }
    }

    public void removePlayer(UUID player) {
        this.players.remove(player);
        Warlords.removePlayer(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            WarlordsEvents.joinInteraction(p);
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
     * @return true is they are on the same team (eg BLUE && BLUE, RED && RED or &lt;not player> && &lt;not playing>
     * @deprecated Use WarLordsPlayer.isTeammate instead
     */
    @Deprecated
    public boolean onSameTeam(@Nonnull WarlordsPlayer player1, @Nonnull WarlordsPlayer player2) {
        return onSameTeam(player1.getUuid(), player2.getUuid());
    }

    public boolean isGameFreeze() {
        return gameFreeze;
    }

    public void setGameFreeze(boolean gameFreeze) {
        this.gameFreeze = gameFreeze;
    }

    public List<OfflinePlayer> getSpectators() {
        return spectators;
    }

    public void addSpectator(Player player) {
        spectators.add(player);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(this.getMap().blueRespawn);
        Warlords.setRejoinPoint(player.getUniqueId(), this.getMap().blueRespawn);
        if(state instanceof PreLobbyState) {
            ((PreLobbyState) state).giveLobbyScoreboard(true, player);
        } else if(state instanceof PlayingState) {
            ((PlayingState) state).updateBasedOnGameState(true, Warlords.playerScoreboards.get(player.getUniqueId()), null);
        }
    }

    public void removeSpectator(Player player, boolean fromMenu) {
        if(fromMenu) {
            spectators.remove(player);
        }
        Location loc = Warlords.spawnPoints.remove(player.getUniqueId());
        Player p = Bukkit.getPlayer(player.getUniqueId());
        if (p != null) {
            if(loc != null) {
                p.teleport(Warlords.getRejoinPoint(p.getUniqueId()));
            }
            WarlordsEvents.joinInteraction(p);
        }
    }

    @Override
    public void run() {
        if (this.state == null) {
            this.state = new InitState(this);
            this.state.begin();
        }
        if (!gameFreeze) {
            State newState = state.run();
            if (newState != null) {
                this.state.end();
                this.state = newState;
                newState.begin();
            }
        }

    }
}