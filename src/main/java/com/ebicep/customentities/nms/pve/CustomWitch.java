package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.World;

public class CustomWitch extends EntityWitch implements CustomEntity<CustomWitch> {

    public CustomWitch(World world) {
        super(world);
        resetAI(world);
        giveBaseAI();
    }

    @Override
    public CustomWitch get() {
        return this;
    }
}
