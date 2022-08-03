package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;

import java.util.UUID;

public class LegendaryWeapon extends AbstractLegendaryWeapon {

    public static final int MELEE_DAMAGE_MIN = 160;
    public static final int MELEE_DAMAGE_MAX = 180;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 200;
    public static final int HEALTH_BONUS = 600;
    public static final int SPEED_BONUS = 10;

    public LegendaryWeapon() {
    }

    public LegendaryWeapon(UUID uuid) {
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
        return 20;
    }

}
