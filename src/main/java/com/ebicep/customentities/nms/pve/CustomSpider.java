package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.*;

public class CustomSpider extends EntitySpider implements CustomEntity<CustomSpider> {

    public CustomSpider(World world) {
        super(world);
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, true));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 80, false));
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 0.8));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 80.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
    }

    @Override
    public CustomSpider get() {
        return this;
    }
}
