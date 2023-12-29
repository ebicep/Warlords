package com.ebicep.customentities.nms.pve;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CustomGuardian extends Guardian {

    public CustomGuardian(World world) {
        super(EntityType.GUARDIAN, ((CraftWorld) world).getHandle());
        this.moveControl = new GuardianMoveControl(this);
        setInvisible(true);
    }

    @Override
    public void setInvisible(boolean invisible) {
        persistentInvisibility = invisible;
        super.setInvisible(invisible);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, this.guardianAttackGoal = new GuardianAttackGoal(this));
    }

    public boolean setLaser(boolean activated) {
        if (activated) {
            LivingEntity target = this.getTarget();
            if (target == null) {
                return false;
            }

            setActiveAttackTarget(target.getId());
        } else {
            setActiveAttackTarget(0);
        }
        return true;
    }

    public void setLaserTicks(int ticks) {
        Preconditions.checkArgument(ticks >= -10, "ticks must be >= %s. Given %s", -10, ticks);
        guardianAttackGoal.attackTime = ticks;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        setTarget(target, EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    @Override
    public void aiStep() {

    }

    private static class GuardianMoveControl extends MoveControl {

        private final CustomGuardian guardian;

        public GuardianMoveControl(CustomGuardian guardian) {
            super(guardian);
            this.guardian = guardian;
        }

        @Override
        public void tick() {

        }
    }

    public static class GuardianAttackGoal extends Guardian.GuardianAttackGoal {

        public int attackTime;
        private final Guardian guardian;
        private final boolean elder;

        public GuardianAttackGoal(Guardian guardian) {
            super(guardian);
            this.guardian = guardian;
            this.elder = guardian instanceof ElderGuardian;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity entityLiving = this.guardian.getTarget();

            return entityLiving != null && entityLiving.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && (this.elder || this.guardian.getTarget() != null && this.guardian.distanceToSqr(this.guardian.getTarget()) > 9.0D);
        }

        @Override
        public void start() {
            this.attackTime = -10;
            this.guardian.getNavigation().stop();
            LivingEntity entityLiving = this.guardian.getTarget();

            if (entityLiving != null) {
                this.guardian.getLookControl().setLookAt(entityLiving, 90.0F, 90.0F);
            }

            this.guardian.hasImpulse = true;
        }

        @Override
        public void stop() {
            this.guardian.setActiveAttackTarget(0);
            this.guardian.setTarget(null);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity entityLiving = this.guardian.getTarget();

            if (entityLiving == null) {
                return;
            }
            this.guardian.getNavigation().stop();
            this.guardian.getLookControl().setLookAt(entityLiving, 90.0F, 90.0F);
            if (!this.guardian.hasLineOfSight(entityLiving)) {
                this.guardian.setTarget(null);
                return;
            }
            ++this.attackTime;
            if (this.attackTime == 0) {
                this.guardian.setActiveAttackTarget(entityLiving.getId());
                if (!this.guardian.isSilent()) {
                    this.guardian.level().broadcastEntityEvent(this.guardian, (byte) 21);
                }
            } else if (this.attackTime >= this.guardian.getAttackDuration()) {
                this.guardian.setTarget(null);
            }
            super.tick();
        }
    }

}
