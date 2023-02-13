package com.ebicep.customentities.nms.pve;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;

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

//    static class PathfinderGoalSpiderMeleeAttack extends PathfinderGoalMeleeAttack {
//        public PathfinderGoalSpiderMeleeAttack(EntitySpider entityspider, Class<? extends Entity> oclass) {
//            super(entityspider, oclass, 1.0, true);
//        }
//
//        public boolean b() {
//            float f = this.b.c(1.0F);
//            if (f >= 0.5F && this.b.bc().nextInt(100) == 0) { //something with light level, cant attack in daylight
//                this.b.setGoalTarget(null);
//                return false;
//            } else {
//                return super.b();
//            }
//        }
//
//        protected double a(LivingEntity entityliving) {
//            return (double) (4.0F + entityliving.width);
//        }
//    }
//
//    static class PathfinderGoalSpiderNearestAttackableTarget<T extends LivingEntity> extends PathfinderGoalNearestAttackableTarget<T> {
//        public PathfinderGoalSpiderNearestAttackableTarget(EntitySpider entityspider, Class<T> oclass) {
//            super(entityspider, oclass, true);
//        }
//
//        public boolean a() {
//            float f = this.e.c(1.0F); //something with light level, cant attack in daylight
//            return !(f >= 0.5F) && super.a();
//        }
//    }
}
