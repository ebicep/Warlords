package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.World;

public class CustomIronGolem extends EntityIronGolem implements CustomEntity<CustomIronGolem> {

    public CustomIronGolem(World world) {
        super(world);
        resetAI(world);
        giveBaseAI(1.0, 0.6);
    }

    @Override
    public CustomIronGolem get() {
        return this;
    }
}
