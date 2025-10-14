package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.java.MathUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public interface DamageCheck {

    DamageCheck DAMAGE_CHECK = new DamageCheck() {};

    float MINIMUM_DAMAGE = 100;
    float MAXIMUM_DAMAGE = 5000;

    float intervalOne = 3000;
    float intervalTwo = 4000;

    static float clamp(float value) {
        if (value > MAXIMUM_DAMAGE) {
            // max damage + value reduced by 90%
            return intervalOne + (value * 0.1f);
        }
        if (value > intervalTwo) {
            // max damage + value reduced by 80%
            return intervalOne + (value * 0.2f);
        }
        if (value > intervalOne) {
            // max damage + value reduced by 70%
            return intervalOne + (value * 0.3f);
        }
        return MathUtils.clamp(value, DamageCheck.MINIMUM_DAMAGE, DamageCheck.MAXIMUM_DAMAGE);
    }

}
