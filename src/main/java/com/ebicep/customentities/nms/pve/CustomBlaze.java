package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.World;

public class CustomBlaze extends EntityBlaze implements CustomEntity<EntityBlaze> {

    public CustomBlaze(World world) {
        super(world);
    }

    @Override
    public EntityBlaze get() {
        return this;
    }

}
