package com.ebicep.customentities.nms;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Input;
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
        requestImmediatePosition(mount);
    }

    /**
     * These mobs are moved on the server, and the client otherwise eases each update over three ticks.
     * A precise position sync every ridden tick makes the client take the new position immediately.
     */
    public static void requestImmediatePosition(Mob mount) {
        mount.setRequiresPrecisePosition(true);
        mount.needsSync = true;
    }

    /**
     * Same strafe and forward shaping as a horse, with no jump component.
     * The dedicated server never copies key state into {@code xxa}/{@code zza}; that lives on the last client input.
     */
    public static Vec3 riddenInput(Player player) {
        float strafe;
        float forward;
        if (player instanceof ServerPlayer serverPlayer) {
            Input input = serverPlayer.getLastClientInput();
            strafe = input.left() == input.right() ? 0.0F : (input.left() ? 1.0F : -1.0F);
            forward = input.forward() == input.backward() ? 0.0F : (input.forward() ? 1.0F : -1.0F);
        } else {
            strafe = player.xxa;
            forward = player.zza;
        }
        strafe *= 0.5F;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        return new Vec3(strafe, 0.0, forward);
    }

    public static float riddenSpeed(Mob mount) {
        return (float) mount.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    /**
     * One tick of full forward input, using the same speed and input shaping as a ridden mount.
     */
    public static void simulateForwardTick(Mob mount) {
        mount.setSpeed(riddenSpeed(mount));
        mount.travel(new Vec3(0.0, 0.0, 1.0));
    }

}
