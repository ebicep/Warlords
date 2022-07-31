package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.World;

public class CustomGuardian extends EntityGuardian implements CustomEntity<CustomGuardian> {

    public CustomGuardian(World world) {
        super(world);
    }


    @Override
    public CustomGuardian get() {
        return this;
    }

}
