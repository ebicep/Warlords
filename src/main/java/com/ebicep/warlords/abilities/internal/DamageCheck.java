package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.util.java.MathUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public interface DamageCheck {

    DamageCheck DAMAGE_CHECK = new DamageCheck() {};

    float MINIMUM_DAMAGE = 100;
    float MAXIMUM_DAMAGE = 3000;

    static float clamp(float value) {
        // max cap + 2% effectiveness
        if (value > MAXIMUM_DAMAGE) {
            return MAXIMUM_DAMAGE + (value * 0.02f);
        }
        return MathUtils.clamp(value, DamageCheck.MINIMUM_DAMAGE, DamageCheck.MAXIMUM_DAMAGE);
    }
}
