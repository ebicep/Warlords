package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;

public class CustomPillager extends Pillager implements CustomEntity<CustomPillager> {

    public CustomPillager(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public CustomPillager(ServerLevel serverLevel) {
        super(EntityType.PILLAGER, serverLevel);
        resetAI();
        giveBaseAI(1.0, 0.6, 100);

    }

    @Override
    public CustomPillager get() {
        return null;
    }
}
