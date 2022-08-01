package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomWolf extends EntityWolf implements CustomEntity<CustomWolf> {

    public CustomWolf(World world) {
        super(world);
        resetAI(world);
        giveBaseAI();
    }

    public CustomWolf(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomWolf get() {
        return this;
    }
}
