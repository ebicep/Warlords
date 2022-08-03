package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendarySuspicious extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 180;
    public static final int MELEE_DAMAGE_MAX = 200;
    public static final int CRIT_CHANCE = 50;
    public static final int CRIT_MULTIPLIER = -50;
    public static final int HEALTH_BONUS = 500;
    public static final int SPEED_BONUS = 8;
    public static final int ENERGY_PER_HIT_BONUS = 5;
    public static final int SKILL_CRIT_CHANCE_BONUS = 5;
    public static final int SKILL_CRIT_MULTIPLIER_BONUS = 15;

    public LegendarySuspicious() {
    }

    public LegendarySuspicious(UUID uuid) {
        super(uuid);
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
        this.energyPerHitBonus = ENERGY_PER_HIT_BONUS;
        this.skillCritChanceBonus = SKILL_CRIT_CHANCE_BONUS;
        this.skillCritMultiplierBonus = SKILL_CRIT_MULTIPLIER_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}
