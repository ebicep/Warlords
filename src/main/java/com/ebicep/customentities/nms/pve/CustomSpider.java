package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class CustomSpider extends EntitySpider implements CustomEntity<CustomSpider> {

    public CustomSpider(World world) {
        super(world);
        resetAI(world);
        giveBaseAI(1.0, 0.8, 20);

        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
//        this.goalSelector.a(4, new PathfinderGoalSpiderMeleeAttack(this, EntityHuman.class));
//        this.targetSelector.a(2, new PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityHuman.class));
    }

    @Override
    public boolean n() {
        return false; //disables spider climbing
    }

    public CustomSpider(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    @Override
    public CustomSpider get() {
        return this;
    }

    static class PathfinderGoalSpiderMeleeAttack extends PathfinderGoalMeleeAttack {
        public PathfinderGoalSpiderMeleeAttack(EntitySpider entityspider, Class<? extends Entity> oclass) {
            super(entityspider, oclass, 1.0, true);
        }

        public boolean b() {
            float f = this.b.c(1.0F);
            if (f >= 0.5F && this.b.bc().nextInt(100) == 0) { //something with light level, cant attack in daylight
                this.b.setGoalTarget(null);
                return false;
            } else {
                return super.b();
            }
        }

        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.width);
        }
    }

    static class PathfinderGoalSpiderNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {
        public PathfinderGoalSpiderNearestAttackableTarget(EntitySpider entityspider, Class<T> oclass) {
            super(entityspider, oclass, true);
        }

        public boolean a() {
            float f = this.e.c(1.0F); //something with light level, cant attack in daylight
            return !(f >= 0.5F) && super.a();
        }
    }
}
