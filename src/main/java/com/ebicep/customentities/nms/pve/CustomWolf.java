package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.World;

public class CustomWolf extends EntityWolf implements CustomEntity<CustomWolf> {

    public CustomWolf(World world) {
        super(world);
        resetAI(world);
        giveBaseAI();
    }

    @Override
    public CustomWolf get() {
        return this;
    }
}
