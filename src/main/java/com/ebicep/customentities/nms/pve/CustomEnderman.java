package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.World;

public class CustomEnderman extends EntityEnderman implements CustomEntity<CustomEnderman> {

    public CustomEnderman(World world) {
        super(world);
        resetAI(world);
        giveBaseAI();
    }


    @Override
    public CustomEnderman get() {
        return this;
    }

}
