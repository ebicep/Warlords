package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Evoker;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

public class CustomEvoker extends Evoker implements CustomEntity<CustomEvoker> {

    public CustomEvoker(ServerLevel serverLevel) {
        super(EntityType.EVOKER, serverLevel);
    }

    public CustomEvoker(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomEvoker get() {
        return this;
    }
}
