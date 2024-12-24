package com.ebicep.holograms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.ReflectionUtils;
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

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager implements Listener {

    static int entityId = Integer.MAX_VALUE / 8;
    private static final Map<String, Hologram> HOLOGRAMS = new ConcurrentHashMap<>();
    private static BukkitTask TASK;
    private static PacketAdapter packetListener;

    public static void init() {
        TASK = new BukkitRunnable() {

            @Override
            public void run() {
                HOLOGRAMS.forEach((s, hologram) -> {
                    Location location = hologram.getLocation();
                    World world = location.getWorld();
                    VisibilityManager visibilityManager = hologram.getVisibilityManager();
                    world.getPlayers().forEach(player -> {
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
                    });
                });
            }

        }.runTaskTimerAsynchronously(Warlords.getInstance(), 0, 0);
        packetListener = new PacketAdapter(Warlords.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket().deepClone();
                int entityID = packet.getIntegers().read(0);
                HOLOGRAMS.forEach((s, hologram) -> {
                    InteractManager interactManager = hologram.getInteractManager();
                    if (interactManager == null) {
                        return;
                    }
                    if (!interactManager.getIds().contains(entityID)) {
                        return;
                    }
                    interactManager.getOnClick().accept(player);
                });
            }
        };
        PacketUtils.PROTOCOL_MANAGER.addPacketListener(packetListener);
        Warlords.getInstance().getServer().getPluginManager().registerEvents(new HologramManager(), Warlords.getInstance());
    }

    public static void cleanup() {
        TASK.cancel();
        HOLOGRAMS.clear();
        PacketUtils.PROTOCOL_MANAGER.removePacketListener(packetListener);
    }

    public static Hologram getHologram(String id) {
        return HOLOGRAMS.get(id);
    }

    public static void addHologram(String id, Hologram.Builder hologramBuilder) {
        addHologram(id, hologramBuilder.createHologram());
    }

    public static void addHologram(String id, Hologram hologram) {
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

    private static void showHologram(Player player, Hologram hologram) {
        ChatUtils.MessageType.HOLOGRAMS.sendMessage("Showing hologram " + hologram.getName());
        HologramData data = hologram.getDataForPlayer(player);
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
                                location.getYaw(),
                                location.getPitch(),
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
                                data.getData()
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
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                player,
                PacketContainer.fromPacket(
                        new ClientboundRemoveEntitiesPacket(
                                hologram.getId() // TODO batch
                        )
                )
        );
        hologram.getVisibilityManager().removeCurrentViewer(player.getUniqueId());
    }

}
