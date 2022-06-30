package com.ebicep.warlords.pve.weapons;

public class RareWeapon extends AbstractWeapon {

    public static final int MELEE_DAMAGE_MIN = 100;
    public static final int MELEE_DAMAGE_MAX = 150;
    public static final int CRIT_CHANCE_MIN = 10;
    public static final int CRIT_CHANCE_MAX = 15;
    public static final int CRIT_MULTIPLIER_MIN = 140;
    public static final int CRIT_MULTIPLIER_MAX = 170;
    public static final int HEALTH_BONUS_MIN = 120;
    public static final int HEALTH_BONUS_MAX = 180;

    @Override
    public void generateStats() {
        this.meleeDamage = generateRandomValueBetween(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX);
        this.critChance = generateRandomValueBetween(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = generateRandomValueBetween(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = generateRandomValueBetween(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
    }
}
