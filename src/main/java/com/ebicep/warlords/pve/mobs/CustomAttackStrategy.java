package com.ebicep.warlords.pve.mobs;

import net.citizensnpcs.api.ai.AttackStrategy;
import net.citizensnpcs.util.PlayerAnimation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;

public class CustomAttackStrategy {

    public static final AttackStrategy ATTACK_STRATEGY = (attacker, btarget) -> {
        LivingEntity source = getHandle(attacker);
        LivingEntity target = getHandle(btarget);
        if (source instanceof ServerPlayer serverPlayer) {
            serverPlayer.attack(target);
            PlayerAnimation.ARM_SWING.play(serverPlayer.getBukkitEntity());
            return true;
        }
        if (source instanceof net.minecraft.world.entity.Mob mob) {
            mob.swing(InteractionHand.MAIN_HAND);
            mob.doHurtTarget(target);
            return true;
        }
        target.hurt(target.damageSources().mobAttack(source), 1);
        return true;
    };

    private static LivingEntity getHandle(org.bukkit.entity.LivingEntity entity) {
        return (LivingEntity) getHandle((org.bukkit.entity.Entity) entity);
    }

    public static Entity getHandle(org.bukkit.entity.Entity entity) {
        if (!(entity instanceof CraftEntity)) {
            return null;
        }
        return ((CraftEntity) entity).getHandle();
    }


}
