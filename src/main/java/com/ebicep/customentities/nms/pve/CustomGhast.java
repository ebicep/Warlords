package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.World;

public class CustomGhast extends EntityGhast implements CustomEntity<CustomGhast> {

    public CustomGhast(World world) {
        super(world);
    }

    @Override
    public CustomGhast get() {
        return this;
    }

}
