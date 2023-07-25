package com.ebicep.customentities.nms.pve;


import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.CaveSpider;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import javax.annotation.Nonnull;

public class CustomCaveSpider extends CaveSpider implements CustomEntity<CustomCaveSpider> {

    public CustomCaveSpider(ServerLevel serverLevel) {
        super(EntityType.CAVE_SPIDER, serverLevel);
        resetAI();
        giveBaseAI(1.0, 0.8, 100);

        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
//        this.goalSelector.a(4, new PathfinderGoalSpiderMeleeAttack(this, EntityHuman.class));
//        this.targetSelector.a(2, new PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityHuman.class));
    }

    @Override
    public boolean isClimbing() {
        return false;
    }

    public CustomCaveSpider(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomCaveSpider get() {
        return this;
    }

    private boolean stunned;

    @Override
    public boolean canCollideWithBukkit(@Nonnull Entity entity) {
        return !stunned;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public DisguiseType getDisguiseType() {
        return DisguiseType.CAVE_SPIDER;
    }
}
