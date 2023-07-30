package com.ebicep.warlords.util.bukkit;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.RelativeMovement;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;

public class TeleportUtils {

    public static void smoothTeleport(Player player, Location location) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        if (serverPlayer.containerMenu != serverPlayer.inventoryMenu) {
            serverPlayer.closeContainer();
        }
        connection.teleport(
                x,
                y,
                z,
                serverPlayer.getYRot(),
                serverPlayer.getXRot(),
                Set.of(RelativeMovement.X_ROT, RelativeMovement.Y_ROT),
                PlayerTeleportEvent.TeleportCause.PLUGIN
        );
    }

}