package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryVorpal extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 200;
    public static final int MELEE_DAMAGE_MAX = 220;
    public static final int CRIT_CHANCE = 35;
    public static final int CRIT_MULTIPLIER = 245;
    public static final int HEALTH_BONUS = 300;
    public static final int SPEED_BONUS = 14;

    public LegendaryVorpal() {
    }

    public LegendaryVorpal(UUID uuid) {
        super(uuid);
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}
