package com.ebicep.customentities.nms;

import net.minecraft.world.entity.Mob;
import org.bukkit.Location;

final class MountEntitySetup {

    private MountEntitySetup() {
    }

    static void prepare(Mob entity, Location location) {
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

}
