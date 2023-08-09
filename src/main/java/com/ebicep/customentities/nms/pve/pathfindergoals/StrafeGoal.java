package com.ebicep.customentities.nms.pve.pathfindergoals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.NodeEvaluator;

import java.util.EnumSet;

/**
 * @see net.minecraft.world.entity.ai.goal.RangedBowAttackGoal
 */
public class StrafeGoal extends Goal {

    private final Mob mob;
    private final float attackRadiusSqr = 15 * 15; // TODO?
    private final double speedModifier = .5;

    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public StrafeGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity != null) {
            double distance = this.mob.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
            boolean hasLineOfSight = this.mob.getSensing().hasLineOfSight(livingEntity);
            boolean isSeeing = this.seeTime > 0;
            if (hasLineOfSight != isSeeing) {
                this.seeTime = 0;
            }

            if (hasLineOfSight) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(distance > (double) this.attackRadiusSqr) && this.seeTime >= 20) {
                this.mob.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.mob.getNavigation().moveTo(livingEntity, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.mob.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (distance > (double) (this.attackRadiusSqr * 0.75F)) {
                    this.strafingBackwards = false;
                } else if (distance < (double) (this.attackRadiusSqr * 0.25F)) {
                    this.strafingBackwards = true;
                }
                float strafeAmount = 5.7F;
//                this.mob.setZza(this.strafingBackwards ? -strafeAmount : strafeAmount);
//                this.mob.setXxa(this.strafingClockwise ? strafeAmount : -strafeAmount);
                float strafeForwards = this.strafingBackwards ? -strafeAmount : strafeAmount;
                float strafeRight = this.strafingClockwise ? strafeAmount : -strafeAmount;
                //   this.mob.getMoveControl().strafe(strafeForwards, strafeRight);
                float f = (float) this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                float g = (float) this.speedModifier * f;
//                float h = strafeForwards;
//                float i = strafeRight;
//                float j = Mth.sqrt(h * h + i * i);
//                if (j < 1.0F) {
//                    j = 1.0F;
//                }
//
//                j = g / j;
//                h *= j;
//                i *= j;
//                float k = Mth.sin(this.mob.getYRot() * ((float)Math.PI / 180F));
//                float l = Mth.cos(this.mob.getYRot() * ((float)Math.PI / 180F));
//                float m = h * l - i * k;
//                float n = i * l + h * k;
//                if (!isWalkable(m, n)) {
//                    strafeForwards = 1.0F;
//                    strafeRight = 0.0F;
//                }
                mob.setSpeed(g);
                mob.setZza(strafeForwards);
                mob.setXxa(strafeRight);
            }
        }
    }

    private boolean isWalkable(float x, float z) {
        PathNavigation pathNavigation = this.mob.getNavigation();
        if (pathNavigation != null) {
            NodeEvaluator nodeEvaluator = pathNavigation.getNodeEvaluator();
            if (nodeEvaluator != null && nodeEvaluator.getBlockPathType(this.mob.level(),
                    Mth.floor(this.mob.getX() + (double) x),
                    this.mob.getBlockY(),
                    Mth.floor(this.mob.getZ() + (double) z)
            ) != BlockPathTypes.WALKABLE) {
                return false;
            }
        }

        return true;
    }
}
