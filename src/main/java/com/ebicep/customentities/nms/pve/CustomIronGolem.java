package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomIronGolem extends EntityIronGolem implements CustomEntity<CustomIronGolem> {

    public CustomIronGolem(World world) {
        super(world);
        resetAI(world);
        giveBaseAI(1.0, 0.6);
    }

    public CustomIronGolem(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }


    @Override
    public CustomIronGolem get() {
        return this;
    }
}
