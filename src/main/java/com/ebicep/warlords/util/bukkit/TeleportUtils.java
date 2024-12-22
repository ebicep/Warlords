package com.ebicep.warlords.util.bukkit;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

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
                new PositionMoveRotation(
                        new Vec3(x, y, z),
                        Vec3.ZERO,
                        serverPlayer.getYRot(),
                        serverPlayer.getXRot()
                ),
                Relative.ROTATION
        );
    }

}