package com.ebicep.warlords.player.ingame.motionsystem;

import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.MotionAddon;

/**
 * Marks a knockback modifier as full immunity. While it is present, other knockback
 * sources cannot change the resulting multiplier.
 */
public final class KnockbackImmunity implements MotionAddon {

    public static final float MODIFIER = -100;

    @Override
    public String addonName() {
        return "Knockback Immunity";
    }

}
