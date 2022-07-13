package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.World;

public class CustomCreeper extends EntityCreeper implements CustomEntity<CustomCreeper> {

    public CustomCreeper(World world) {
        super(world);
    }

    @Override
    public CustomCreeper get() {
        return this;
    }

}
