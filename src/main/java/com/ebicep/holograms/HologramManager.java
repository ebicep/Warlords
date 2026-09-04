package com.ebicep.holograms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedEnumEntityUseAction;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.ReflectionUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager implements Listener {

    private static final int INTERACT_COOLDOWN_MS = 250;
    private static final int TICK_INTERVAL = 10;
    private static final Map<UUID, Long> INTERACT_COOLDOWNS = new ConcurrentHashMap<>();
    private static final Map<String, Hologram> HOLOGRAMS = new ConcurrentHashMap<>();
    static int entityId = Integer.MAX_VALUE / 8;
    private static BukkitTask TASK;
    private static PacketAdapter packetListener;

    public static void init(Warlords instance) {
        TASK = new BukkitRunnable() {

            private int tick = 0;

            @Override
            public void run() {
                Hologram[] holograms = HOLOGRAMS.values().toArray(Hologram[]::new);
                int bucket = tick++ % TICK_INTERVAL;
                for (int i = bucket; i < holograms.length; i += TICK_INTERVAL) {
                    Hologram hologram = holograms[i];
                    Location location = hologram.getLocation();
                    World world = location.getWorld();
                    VisibilityManager visibilityManager = hologram.getVisibilityManager();
                    List<Player> players = new ArrayList<>(world.getPlayers());
                    for (Player player : players) {
                        boolean withinRange = hologram.withinRange(player);
                        boolean currentlyVisibleTo = visibilityManager.isCurrentlyVisibleTo(player);
                        switch (visibilityManager.getVisibilityType()) {
                            case ALL -> {
                                if (!currentlyVisibleTo && withinRange) {
                                    showHologram(player, hologram);
                                } else if (currentlyVisibleTo && !withinRange) {
                                    hideHologram(player, hologram);
                                }
                            }
                            case MANUAL -> {
                                if (visibilityManager.isViewer(player) && !currentlyVisibleTo && withinRange) {
                                    showHologram(player, hologram);
                                } else if (visibilityManager.isViewer(player) && currentlyVisibleTo && !withinRange) {
                                    hideHologram(player, hologram);
                                } else if (!visibilityManager.isViewer(player) && currentlyVisibleTo) {
                                    hideHologram(player, hologram);
                                }
                            }
                        }
                    }
                }
            }

        }.runTaskTimerAsynchronously(instance, 0, 1);
        packetListener = new PacketAdapter(instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket().deepClone();
                int entityID = packet.getIntegers().read(0);
                List<WrappedEnumEntityUseAction> values = event.getPacket().getEnumEntityUseActions().getValues();
                WrappedEnumEntityUseAction wrappedEnumEntityUseAction = values.getFirst();
                if (wrappedEnumEntityUseAction.getAction() == EnumWrappers.EntityUseAction.INTERACT) {
                    return;
                }
                HOLOGRAMS.forEach((s, hologram) -> {
                    InteractManager interactManager = hologram.getInteractManager();
                    if (interactManager == null) {
                        return;
                    }
                    if (!interactManager.getIds().contains(entityID)) {
                        return;
                    }
                    if (INTERACT_COOLDOWNS.computeIfAbsent(player.getUniqueId(), uuid -> 0L) > System.currentTimeMillis()) {
                        return;
                    }
                    INTERACT_COOLDOWNS.put(player.getUniqueId(), System.currentTimeMillis() + INTERACT_COOLDOWN_MS);
                    boolean updateHologram = interactManager.getOnClick().apply(player);
                    if (updateHologram) {
                        updateHologram(player, hologram);
                    }
                });
            }
        };
        PacketUtils.PROTOCOL_MANAGER.addPacketListener(packetListener);
        Warlords.getInstance().getServer().getPluginManager().registerEvents(new HologramManager(), instance);
        ChatUtils.MessageType.HOLOGRAMS.sendMessage("Hologram manager initialized");
    }

    private static void showHologram(Player player, Hologram hologram) {
        HologramData data = hologram.getDataForPlayer(player);
        if (data == null) {
            return;
        }
        ChatUtils.MessageType.HOLOGRAMS.sendMessage("Showing hologram " + hologram.getName());
        Location location = hologram.getLocation();
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                player,
                PacketContainer.fromPacket(
                        new ClientboundAddEntityPacket(
                                hologram.getId(),
                                UUID.randomUUID(),
                                location.getX(),
                                location.getY(),
                                location.getZ(),
                                location.getPitch(),
                                location.getYaw(),
                                data.getEntityType(),
                                0,
                                new Vec3(0, 0, 0),
                                0
                        )
                )
        );
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                player,
                PacketContainer.fromPacket(
                        new ClientboundSetEntityDataPacket(
                                hologram.getId(),
                                data.getData(player)
                        )
                )
        );
        InteractManager interactManager = hologram.getInteractManager();
        if (interactManager != null) {
            InteractData interactData = interactManager.getDataForPlayer(player);
            for (Integer id : interactManager.getIds()) {
                PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                        player,
                        PacketContainer.fromPacket(
                                new ClientboundAddEntityPacket(
                                        id,
                                        UUID.randomUUID(),
                                        location.getX(),
                                        location.getY(),
                                        location.getZ(),
                                        location.getYaw(),
                                        location.getPitch(),
                                        EntityType.INTERACTION,
                                        0,
                                        new Vec3(0, 0, 0),
                                        0
                                )
                        )
                );
                PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                        player,
                        PacketContainer.fromPacket(
                                new ClientboundSetEntityDataPacket(
                                        id,
                                        interactData.getData(hologram, player)
                                )
                        )
                );
            }
        }
        hologram.getVisibilityManager().addCurrentViewer(player.getUniqueId());
    }

    private static void hideHologram(Player player, Hologram hologram) {
        ChatUtils.MessageType.HOLOGRAMS.sendMessage("Hiding hologram " + hologram.getName());
        IntList ids = new IntArrayList();
        ids.add(hologram.getId());
        if (hologram.getInteractManager() != null) {
            ids.addAll(hologram.getInteractManager().getIds());
        }
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                player,
                PacketContainer.fromPacket(new ClientboundRemoveEntitiesPacket(ids))
        );
        hologram.getVisibilityManager().removeCurrentViewer(player.getUniqueId());
    }

    public static void removeInteractCooldown(UUID uuid) {
        INTERACT_COOLDOWNS.remove(uuid);
    }

    public static void updateHologram(Hologram hologram) {
        for (Player player : hologram.getLocation().getWorld().getPlayers()) {
            if (hologram.withinRange(player)) {
                updateHologram(player, hologram);
            }
        }
    }

    public static void updateHologram(Player player, Hologram hologram) {
        HologramData data = hologram.getDataForPlayer(player);
        if (data == null) {
            hideHologram(player, hologram);
            return;
        }
        ChatUtils.MessageType.HOLOGRAMS.sendMessage("Updating hologram " + hologram.getName());
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                player,
                PacketContainer.fromPacket(
                        new ClientboundSetEntityDataPacket(
                                hologram.getId(),
                                data.getData(player)
                        )
                )
        );
        InteractManager interactManager = hologram.getInteractManager();
        if (interactManager != null) {
            InteractData interactData = interactManager.getDataForPlayer(player);
            for (Integer id : interactManager.getIds()) {
                PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                        player,
                        PacketContainer.fromPacket(
                                new ClientboundSetEntityDataPacket(
                                        id,
                                        interactData.getData(hologram, player)
                                )
                        )
                );
            }
        }
    }

    public static void cleanup() {
        TASK.cancel();
        HOLOGRAMS.clear();
        PacketUtils.PROTOCOL_MANAGER.removePacketListener(packetListener);
    }

    public static Hologram getHologram(String id) {
        return HOLOGRAMS.get(id);
    }

    public static void addHologram(Hologram hologram) {
        addHologram(hologram.getName(), hologram);
    }

    public static void addHologram(String id, Hologram hologram) {
        ChatUtils.MessageType.HOLOGRAMS.sendMessage("Adding hologram " + hologram.getName());
        if (HOLOGRAMS.containsKey(id)) {
            Hologram oldHologram = HOLOGRAMS.get(id);
            oldHologram.getVisibilityManager().getCurrentViewers().forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    hideHologram(player, oldHologram);
                }
            });
        }
        HOLOGRAMS.put(id, hologram);
    }

    public static void addHologram(String id, Hologram.Builder hologramBuilder) {
        addHologram(id, hologramBuilder.build());
    }

    public static void deleteHologram(String id) {
        Hologram hologram = HOLOGRAMS.remove(id);
        if (hologram != null) {
            hologram.getVisibilityManager().getCurrentViewers().forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    hideHologram(player, hologram);
                }
            });
        }
    }

    public static <T, R> SynchedEntityData.DataValue<?> createDataValue(Class<T> clazz, String variableName, R value) throws NoSuchFieldException, IllegalAccessException {
        return SynchedEntityData.DataValue.create(ReflectionUtils.getStaticField(clazz, variableName), value);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        HOLOGRAMS.forEach((s, hologram) -> {
            if (!Objects.equals(hologram.getLocation().getWorld(), event.getPlayer().getWorld())) {
                return;
            }
            if (!hologram.withinRange(event.getPlayer())) {
                return;
            }
            showHologram(event.getPlayer(), hologram);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HOLOGRAMS.forEach((s, hologram) -> {
            hologram.getVisibilityManager().getCurrentViewers().remove(player.getUniqueId());
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World from = event.getFrom();
        World to = event.getPlayer().getWorld();
        HOLOGRAMS.forEach((s, hologram) -> {
            if (Objects.equals(hologram.getLocation().getWorld(), from)) {
                hologram.getVisibilityManager().getCurrentViewers().remove(player.getUniqueId());
            }
            if (Objects.equals(hologram.getLocation().getWorld(), to) && hologram.withinRange(event.getPlayer())) {
                showHologram(event.getPlayer(), hologram);
            }
        });
    }

}
