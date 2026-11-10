package com.ebicep.customentities.nms;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;

import javax.annotation.Nullable;

public final class MountEntitySetup {

    /**
     * Shared base speed so every species matches the old horse mount.
     */
    public static final double MOVEMENT_SPEED = 0.32;

    private MountEntitySetup() {
    }

    public static void prepare(Mob entity, Location location) {
        float yaw = location.getYaw();
        entity.setPos(location.getX(), location.getY(), location.getZ());
        entity.setRot(yaw, location.getPitch());
        entity.setYBodyRot(yaw);
        entity.setYHeadRot(yaw);
        entity.yBodyRotO = yaw;
        entity.yHeadRotO = yaw;
        entity.setNoAi(true);
        entity.setSilent(true);
        entity.setPersistenceRequired(true);
    }

    /**
     * First player passenger controls the mount, with no saddle or steering item required.
     */
    @Nullable
    public static Player controllingPassenger(Mob mount) {
        if (mount.getFirstPassenger() instanceof Player player) {
            return player;
        }
        return null;
    }

    public static void tickRidden(Mob mount, Player player) {
        float yaw = player.getYRot();
        mount.setRot(yaw, player.getXRot() * 0.5F);
        mount.yRotO = mount.yBodyRot = mount.yHeadRot = yaw;
    }

    /**
     * Same strafe and forward shaping as a horse, with no jump component.
     */
    public static Vec3 riddenInput(Player player) {
        float strafe = player.xxa * 0.5F;
        float forward = player.zza;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        return new Vec3(strafe, 0.0, forward);
    }

    public static float riddenSpeed(Mob mount) {
        return (float) mount.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

}
