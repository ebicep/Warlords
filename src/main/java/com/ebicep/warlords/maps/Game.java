package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.state.*;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PacketUtils;
import net.md_5.bungee.api.chat.TextComponent;
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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Game implements Runnable {

    public static TextComponent spacer = new TextComponent(ChatColor.GRAY + " - ");
    private final Map<UUID, Team> players = new HashMap<>();
    public boolean freezeOnCooldown = false;
    private State state = null;
    private GameMap map = GameMap.RIFT;
    private boolean cooldownMode;
    private boolean gameFreeze = false;
    private boolean isPrivate = false;
    private List<UUID> spectators = new ArrayList<>();
    private HashMap<BukkitTask, Long> gameTasks = new HashMap<>();

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
        return players.isEmpty() && (state instanceof PreLobbyState || state instanceof InitState || state instanceof EndState);
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

    public void freeze(String subtitleMessage, boolean countdown) {
        if (gameFreeze && !freezeOnCooldown) {
            freezeOnCooldown = true;
            //unfreeze
            forEachOnlinePlayer((p, team) -> {
                p.removePotionEffect(PotionEffectType.BLINDNESS);
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 100000));
            });
            if (countdown) {
                new BukkitRunnable() {
                    int counter = 0;

                    @Override
                    public void run() {
                        if (counter >= 5) {
                            setGameFreeze(false);
                            freezeOnCooldown = false;
                            forEachOnlinePlayer((p, team) -> {
                                if (p.getVehicle() != null && p.getVehicle() instanceof Horse) {
                                    ((EntityLiving) ((CraftEntity) p.getVehicle()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(.318);
                                }
                                PacketUtils.sendTitle(p, "", "", 0, 0, 0);
                                p.removePotionEffect(PotionEffectType.BLINDNESS);
                            });
                            this.cancel();
                        } else {
                            forEachOnlinePlayer((p, team) -> {
                                PacketUtils.sendTitle(p, ChatColor.BLUE + "Resuming in... " + ChatColor.GREEN + (5 - counter), "", 0, 40, 0);
                            });
                            counter++;
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 20);
            } else {
                setGameFreeze(false);
                freezeOnCooldown = false;
                forEachOnlinePlayer((p, team) -> {
                    if (p.getVehicle() != null && p.getVehicle() instanceof Horse) {
                        ((EntityLiving) ((CraftEntity) p.getVehicle()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(.318);
                    }
                    PacketUtils.sendTitle(p, "", "", 0, 0, 0);
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                });
            }
        } else {
            //freeze
            setGameFreeze(true);
            forEachOnlinePlayer((p, team) -> freezePlayer(p, subtitleMessage));
        }
    }

    public void freezePlayer(Player p, String subtitleMessage) {
        if (p.getVehicle() instanceof Horse) {
            ((EntityLiving) ((CraftEntity) p.getVehicle()).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 100000));
        PacketUtils.sendTitle(p, ChatColor.RED + "Game Paused", subtitleMessage, 0, 9999999, 0);
    }

    public List<UUID> getSpectators() {
        return spectators;
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

    public HashMap<BukkitTask, Long> getGameTasks() {
        return gameTasks;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    @Override
    public void run() {
        if (this.state == null) {
            this.state = new InitState(this);
            System.out.println("DEBUG NEW STATE");
            System.out.println("New state is " + this.state);
            this.state.begin();
        }
        if (!gameFreeze) {
            State newState = state.run();
            if (newState != null) {
                this.state.end();
                System.out.println("DEBUG OLD TO NEW STATE");
                Command.broadcastCommandMessage(Bukkit.getConsoleSender(), "New State = " + newState + " / Old State = " + this.state);
                this.state = newState;
                newState.begin();
            }
        }

    }
}