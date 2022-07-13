package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.*;

public class CustomEnderman extends EntityEnderman implements CustomEntity<CustomEnderman> {

    public CustomEnderman(World world) {
        super(world);
        this.goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0, true));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 80.0F));
    }


    @Override
    public CustomEnderman get() {
        return this;
    }

}
