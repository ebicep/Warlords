package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.World;

public class CustomPigZombie extends EntityPigZombie implements CustomEntity<CustomPigZombie> {

    public CustomPigZombie(World world) {
        super(world);
    }


    @Override
    public CustomPigZombie get() {
        return this;
    }

}
