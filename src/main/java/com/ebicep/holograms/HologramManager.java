package com.ebicep.holograms;

import com.comphenix.protocol.events.PacketContainer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.bukkit.packets.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.world.phys.Vec3;
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

    private static final Map<String, Hologram> HOLOGRAMS = new ConcurrentHashMap<>();
    private static BukkitTask TASK;

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
                        if (withinRange && !currentlyVisibleTo) {
                            showHologram(player, hologram);
                        } else if (!withinRange && currentlyVisibleTo) {
                            hideHologram(player, hologram);
                        }
                    });
                });
            }

        }.runTaskTimerAsynchronously(Warlords.getInstance(), 0, 0);
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
//        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
//                player,
//                PacketContainer.fromPacket(
//                        new ClientboundSetEntityDataPacket(
//                                hologram.getId(), // TODO
//                        )
//                )
//        );
        hologram.getVisibilityManager().getCurrentViewers().add(player.getUniqueId());
    }

    private static void hideHologram(Player player, Hologram hologram) {
        PacketUtils.PROTOCOL_MANAGER.sendServerPacket(
                player,
                PacketContainer.fromPacket(
                        new ClientboundRemoveEntitiesPacket(
                                hologram.getId() // TODO batch
                        )
                )
        );
        hologram.getVisibilityManager().getCurrentViewers().remove(player.getUniqueId());
    }

}
