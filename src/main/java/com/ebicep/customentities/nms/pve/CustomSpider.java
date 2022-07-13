package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.World;

public class CustomSpider extends EntitySpider implements CustomEntity<CustomSpider> {

    public CustomSpider(World world) {
        super(world);
    }

    @Override
    public CustomSpider get() {
        return this;
    }

}
