package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.World;

public class CustomSlime extends EntitySlime implements CustomEntity<CustomSlime> {

    public CustomSlime(World world) {
        super(world);
        setSize(5);
    }

    //jump
    @Override
    protected void bF() {
        this.motY = 0.1; //motion y
        this.ai = true; //isAirBorne
    }

    @Override
    public CustomSlime get() {
        return this;
    }

}
