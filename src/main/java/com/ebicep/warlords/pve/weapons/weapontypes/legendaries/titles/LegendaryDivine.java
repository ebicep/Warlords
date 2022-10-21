package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryDivine extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 100;
    public static final int MELEE_DAMAGE_MAX = 120;
    public static final int CRIT_CHANCE = 25;
    public static final int CRIT_MULTIPLIER = 175;
    public static final int HEALTH_BONUS = 500;
    public static final int SPEED_BONUS = 5;
    public static final int ENERGY_PER_SECOND_BONUS = 7;
    public static final int ENERGY_PER_HIT_BONUS = -10;
    public static final int SKILL_CRIT_CHANCE_BONUS = 5;

    public LegendaryDivine() {
    }

    public LegendaryDivine(UUID uuid) {
        super(uuid);
    }

    public LegendaryDivine(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getTitle() {
        return "Divine";
    }

    @Override
    public String getPassiveEffect() {
        return "Increase the next ability damage by 2% per targets hit.";
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
        this.energyPerSecondBonus = ENERGY_PER_SECOND_BONUS;
        this.energyPerHitBonus = ENERGY_PER_HIT_BONUS;
        this.skillCritChanceBonus = SKILL_CRIT_CHANCE_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}
