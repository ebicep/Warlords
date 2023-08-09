package com.ebicep.customentities.nms.pve;

import com.ebicep.customentities.nms.pve.pathfindergoals.PathfinderGoalPredictTargetFutureLocationGoal;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.WitherSkeleton;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;

import javax.annotation.Nonnull;

public class CustomWitherSkeleton extends WitherSkeleton implements CustomEntity<CustomWitherSkeleton> {

    private final PathfinderGoalPredictTargetFutureLocationGoal pathfinderGoalPredictTargetFutureLocationGoal = new PathfinderGoalPredictTargetFutureLocationGoal(this);
    private boolean stunned;

    public CustomWitherSkeleton(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public CustomWitherSkeleton(ServerLevel serverLevel) {
        super(EntityType.WITHER_SKELETON, serverLevel);
        resetAI();
        giveBaseAI(1.2, 1.0, 100);
        this.goalSelector.addGoal(2, pathfinderGoalPredictTargetFutureLocationGoal);
    }

    @Override
    public CustomWitherSkeleton get() {
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

    public PathfinderGoalPredictTargetFutureLocationGoal getPathfinderGoalFireAtPlayer() {
        return pathfinderGoalPredictTargetFutureLocationGoal;
    }

    @Override
    public DisguiseType getDisguiseType() {
        return DisguiseType.WITHER_SKELETON;
    }

}

