package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.java.MathUtils;

public interface DamageCheck {

    DamageCheck DAMAGE_CHECK = new DamageCheck() {};

    float MINIMUM_DAMAGE = 100;
    float MAXIMUM_DAMAGE = 10000;

    static float clamp(float value) {
        if (value > MAXIMUM_DAMAGE) {
            // max damage + value reduced by 90%
            return MAXIMUM_DAMAGE + (value * 0.1f);
        }
        return MathUtils.clamp(value, DamageCheck.MINIMUM_DAMAGE, DamageCheck.MAXIMUM_DAMAGE);
    }

}
