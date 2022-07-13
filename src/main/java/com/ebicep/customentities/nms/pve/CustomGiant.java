package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.World;

public class CustomGiant extends EntityGiantZombie implements CustomEntity<CustomGiant> {

    public CustomGiant(World world) {
        super(world);
    }

    @Override
    public CustomGiant get() {
        return this;
    }

}
