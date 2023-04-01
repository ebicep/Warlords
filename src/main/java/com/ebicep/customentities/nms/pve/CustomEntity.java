package com.ebicep.customentities.nms.pve;

import com.ebicep.customentities.nms.pve.pathfindergoals.PathfinderGoalTargetAgroWarlordsEntity;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityTargetEvent;

public interface CustomEntity<T extends EntityInsentient> {

    default EntityLiving getTarget() {
        return get().getGoalTarget();
    }

    default void removeTarget() {
        get().setGoalTarget(null, EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    default void setTarget(Player player) {
        get().setGoalTarget((EntityLiving) ((CraftEntity) player).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    default void setTarget(EntityLiving entityLiving) {
        get().setGoalTarget(entityLiving, EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    default void resetAI(World world) {
        resetGoalAI(world);
        resetTargetAI(world);
    }

    default void resetGoalAI(World world) {
        get().goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
    }

    default void addGoalAI(int priority, PathfinderGoal pathfinderGoal) {
        get().goalSelector.a(priority, pathfinderGoal);
    }

    default void resetTargetAI(World world) {
        get().targetSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
    }

    default void addTargetAI(int priority, PathfinderGoal pathfinderGoal) {
        get().targetSelector.a(priority, pathfinderGoal);
    }

    default void giveBaseAI() {
        giveBaseAI(1.0, 1.0, 100);
    }

    default void giveBaseAI(double speedTowardsTarget, double wanderSpeed, int followRange) {
        T entity = get();
        entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(followRange);
        //float in water
        aiFloat();
        if (entity instanceof EntityCreature) {
            //melee entity within range/onCollide?
            aiMeleeAttack(speedTowardsTarget);
            //wander around
            aiWander(wanderSpeed);

            //targets entity that hit it
            aiTargetHitBy();
            //targets closest entities
            aiTargetClosest();
        }
        //look at player
        aiLookAtPlayer();
        //look idle
        aiLookIdle();
    }

    //GOAL SELECTOR AI
    default void aiFloat() {
        get().goalSelector.a(0, new PathfinderGoalFloat(get()));
    }

    default void aiMeleeAttack(double speedTowardsTarget) {
        T entity = get();
        if (entity instanceof EntityCreature) {
            entity.goalSelector.a(1, new PathfinderGoalMeleeAttack((EntityCreature) entity, EntityLiving.class, speedTowardsTarget, true));
        }
    }

    default void aiWander(double wanderSpeed) {
        T entity = get();
        if (entity instanceof EntityCreature) {
            entity.goalSelector.a(7, new PathfinderGoalRandomStroll((EntityCreature) entity, wanderSpeed));
        }
    }

    default void aiLookAtPlayer() {
        get().goalSelector.a(8, new PathfinderGoalLookAtPlayer(get(), EntityLiving.class, 20.0F));
    }

    default void aiLookIdle() {
        get().goalSelector.a(8, new PathfinderGoalRandomLookaround(get()));
    }

    //TARGET SELECTOR AI
    default void aiTargetHitBy() {
        T entity = get();
        if (entity instanceof EntityCreature) {
            entity.targetSelector.a(1, new PathfinderGoalHurtByTarget((EntityCreature) entity, false, EntityLiving.class));
        }
    }

    default void aiTargetClosest() {
        T entity = get();
        if (entity instanceof EntityCreature) {
//            entity.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>((EntityCreature) entity, EntityHuman.class, 2, false, false, null));
            entity.targetSelector.a(2, new PathfinderGoalTargetAgroWarlordsEntity((EntityCreature) entity));
        }
    }

    default void spawn(Location location) {
        EffectUtils.playCylinderAnimation(location, 1.05, ParticleEffect.SPELL_WITCH, 1);
        Utils.playGlobalSound(location, Sound.GHAST_FIREBALL, 2, 0.25f);
        T customEntity = get();
        customEntity.setPosition(location.getX(), location.getY(), location.getZ());
        customEntity.setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(customEntity);
    }

    default void onDeath(T entity, Location deathLocation, WaveDefenseOption waveDefenseOption) {
    }

    T get();

    default void setStunned(boolean stunned) {

    }
}
