package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryVigorous extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 140;
    public static final int MELEE_DAMAGE_MAX = 170;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 180;
    public static final int HEALTH_BONUS = 600;
    public static final int SPEED_BONUS = 10;
    public static final int ENERGY_PER_SECOND_BONUS = 2;

    public LegendaryVigorous() {
    }

    public LegendaryVigorous(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getPassiveEffect() {
        return "+2 Energy per Second for 10 seconds after using 500 energy. Can be triggered once per 30 seconds.";
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
        this.energyPerSecondBonus = ENERGY_PER_SECOND_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}
