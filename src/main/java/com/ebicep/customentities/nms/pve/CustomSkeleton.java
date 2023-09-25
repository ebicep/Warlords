package com.ebicep.customentities.nms.pve;

import com.ebicep.customentities.nms.pve.pathfindergoals.PredictTargetFutureLocationGoal;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ProjectileWeaponItem;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import javax.annotation.Nonnull;

public class CustomSkeleton extends Skeleton implements CustomEntity<CustomSkeleton> {

    private final PredictTargetFutureLocationGoal predictTargetFutureLocationGoal = new PredictTargetFutureLocationGoal(this);
    private boolean stunned;

    public CustomSkeleton(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public CustomSkeleton(ServerLevel serverLevel) {
        super(EntityType.SKELETON, serverLevel);
        resetAI();
        giveBaseAI(1.2, 1.0, 100);
        this.goalSelector.addGoal(2, predictTargetFutureLocationGoal);
    }

    @Override
    public CustomSkeleton get() {
        return this;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    @Override
    public boolean canCollideWithBukkit(@Nonnull Entity entity) {
        return !stunned;
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {

    }

    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem weapon) {
        return false;
    }

    @Override
    public DisguiseType getDisguiseType() {
        return DisguiseType.SKELETON;
    }

    public PredictTargetFutureLocationGoal getPathfinderGoalFireAtPlayer() {
        return predictTargetFutureLocationGoal;
    }

}

