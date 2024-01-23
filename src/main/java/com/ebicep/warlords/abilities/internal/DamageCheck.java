package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.java.MathUtils;

public interface DamageCheck {
    DamageCheck DAMAGE_CHECK = new DamageCheck() {};

    float MINIMUM_DAMAGE = 100;
    float MAXIMUM_DAMAGE = 500;

    static float clamp(float value) {
        return MathUtils.clamp(value, DamageCheck.MINIMUM_DAMAGE, DamageCheck.MAXIMUM_DAMAGE);
    }

}
