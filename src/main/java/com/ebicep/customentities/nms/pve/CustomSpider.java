package com.ebicep.customentities.nms.pve;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import javax.annotation.Nonnull;

public class CustomSpider extends Spider implements CustomEntity<CustomSpider> {

    public CustomSpider(ServerLevel serverLevel) {
        super(EntityType.SPIDER, serverLevel);
        resetAI();
        giveBaseAI(1.0, 0.8, 100);

        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
//        this.goalSelector.a(4, new PathfinderGoalSpiderMeleeAttack(this, EntityHuman.class));
//        this.targetSelector.a(2, new PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityHuman.class));
    }

    @Override
    public boolean isClimbing() {
        return false; //spiders cant climb
    }

    public CustomSpider(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomSpider get() {
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
    public DisguiseType getDisguiseType() {
        return DisguiseType.SPIDER;
    }

}
