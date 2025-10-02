package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;

public class MaxHealthDamage {
    //this formula will define max hp damage 1800 * ln(1 + maxHp / 6500)
    //any ability that deals max hp damage should display "Deals x% of Max HP Damage"
    private static float calculateMaxHealthDamage(double maxHp) {
        return (float) (1800 * Math.log(1 + maxHp / 6500.0));
    }

    public static float getMaxHealthDamage(WarlordsEntity entity, float maxHealthDamageMultiplier) {
        return calculateMaxHealthDamage(entity.getMaxHealth())*maxHealthDamageMultiplier;
    }
}