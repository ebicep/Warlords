package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryDivine extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 100;
    public static final int MELEE_DAMAGE_MAX = 120;
    public static final int CRIT_CHANCE = 10;
    public static final int CRIT_MULTIPLIER = 150;
    public static final int HEALTH_BONUS = 600;
    public static final int SPEED_BONUS = 5;
    public static final int ENERGY_PER_SECOND_BONUS = 5;
    public static final int ENERGY_PER_HIT_BONUS = -5;
    public static final int SKILL_CRIT_CHANCE_BONUS = 5;

    public LegendaryDivine() {
    }

    public LegendaryDivine(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getTitle() {
        return "Divine";
    }

    @Override
    public String getPassiveEffect() {
        return "Increase the next ability damage by 1% per targets hit.";
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
