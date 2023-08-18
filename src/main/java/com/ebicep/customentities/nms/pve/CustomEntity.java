package com.ebicep.customentities.nms.pve;

import com.ebicep.customentities.nms.pve.pathfindergoals.PathfinderGoalTargetAgroWarlordsEntity;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.util.warlords.Utils;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;

public interface CustomEntity<T extends Mob> {

    default LivingEntity getTarget() {
        return get().getTarget();
    }

    default void removeTarget() {
        get().setTarget(null, EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    default void setTarget(Player player) {
        get().setTarget((LivingEntity) ((CraftEntity) player).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    default void setTarget(LivingEntity entityLiving) {
        get().setTarget(entityLiving, EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    default void resetAI() {
        resetGoalAI();
        resetTargetAI();
    }

    default void resetGoalAI() {
        get().removeAllGoals(goal -> true);
    }

    default void addGoalAI(int priority, Goal goal) {
        get().goalSelector.addGoal(priority, goal);
    }

    default void resetTargetAI() {
        get().targetSelector.removeAllGoals(goal -> true);
    }

    default void addTargetAI(int priority, Goal goal) {
        get().targetSelector.addGoal(priority, goal);
    }

    default void giveBaseAI() {
        giveBaseAI(1.0, 1.0, 100);
    }

    default void giveBaseAI(double speedTowardsTarget, double wanderSpeed, int followRange) {
        T entity = get();
        entity.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(followRange);
        //float in water
        aiFloat();
        if (entity instanceof PathfinderMob) {
            //melee entity within range/onCollide?
            aiMeleeAttack(speedTowardsTarget);
            //wander around
            aiWander(wanderSpeed);

            //targets entity that hit it
            //aiTargetHitBy();
        }
        //targets closest entities
        aiTargetClosest();
        //look at player
        aiLookAtPlayer();
        //look idle
        aiLookIdle();
    }

    //GOAL SELECTOR AI
    default void aiFloat() {
        get().goalSelector.addGoal(0, new FloatGoal(get()));
    }

    default void aiMeleeAttack(double speedTowardsTarget) {
        T entity = get();
        if (entity instanceof PathfinderMob) {
            entity.goalSelector.addGoal(1, new MeleeAttackGoal((PathfinderMob) entity, speedTowardsTarget, true));
        }
    }

    default void aiWander(double wanderSpeed) {
        T entity = get();
        if (entity instanceof PathfinderMob) {
            entity.goalSelector.addGoal(7, new RandomStrollGoal((PathfinderMob) entity, wanderSpeed));
        }
    }

    default void aiLookAtPlayer() {
        get().goalSelector.addGoal(8, new LookAtPlayerGoal(get(), LivingEntity.class, 20.0F));
    }

    default void aiLookIdle() {
        get().goalSelector.addGoal(8, new RandomLookAroundGoal(get()));
    }

    //TARGET SELECTOR AI
    default void aiTargetHitBy() {
        T entity = get();
        if (entity instanceof PathfinderMob) {
            entity.targetSelector.addGoal(1, new HurtByTargetGoal((PathfinderMob) entity, LivingEntity.class));
        }
    }

    default void aiTargetClosest() {
        T entity = get();
        entity.targetSelector.addGoal(2, new PathfinderGoalTargetAgroWarlordsEntity(entity));
    }

    default void spawn(Location location) {
        EffectUtils.playCylinderAnimation(location, 1.05, Particle.SPELL_WITCH, 1);
        Utils.playGlobalSound(location, Sound.ENTITY_GHAST_SHOOT, 2, 0.25f);
        T customEntity = get();
        customEntity.setPos(location.getX(), location.getY(), location.getZ());
        customEntity.setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(customEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    default void onDeath(T entity, Location deathLocation, PveOption pveOption) {
    }

    T get();

    default void setStunned(boolean stunned) {

    }

    default DisguiseType getDisguiseType() {
        return null;
    }
}
