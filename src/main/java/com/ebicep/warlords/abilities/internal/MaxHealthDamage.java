package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;

public class MaxHealthDamage {
    //MAX HEALTH DAMAGE = 1600 * ln(1 + maxHp / 6500)
    //any ability that uses max health damage shows "Deals x% of MAX HEALTH DAMAGE"
    private static float calculateMaxHealthDamage(double maxHp) {
        return (float) (1600 * Math.log(1 + maxHp / 6500.0));
    }

    public static float getMaxHealthDamage(WarlordsEntity entity, float maxHealthDamageMultiplier) {
        return calculateMaxHealthDamage(entity.getMaxHealth())*maxHealthDamageMultiplier;
    }
}