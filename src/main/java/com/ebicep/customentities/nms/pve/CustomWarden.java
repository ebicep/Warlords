package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.warden.Warden;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

public class CustomWarden extends Warden implements CustomEntity<CustomWarden> {

    public CustomWarden(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public CustomWarden(ServerLevel serverLevel) {
        super(EntityType.WARDEN, serverLevel);
        resetAI();
        giveBaseAI(1.0, 0.6, 100);

    }

    @Override
    public CustomWarden get() {
        return this;
    }
}
