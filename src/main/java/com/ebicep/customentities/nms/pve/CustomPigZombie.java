package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.*;

public class CustomPigZombie extends EntityPigZombie implements CustomEntity<CustomPigZombie> {

    public CustomPigZombie(World world) {
        super(world);
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true, true));
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 80, false));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 80.0F));
    }


    @Override
    public CustomPigZombie get() {
        return this;
    }

}
