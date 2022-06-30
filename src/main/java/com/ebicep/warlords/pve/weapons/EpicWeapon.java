package com.ebicep.warlords.pve.weapons;

public class EpicWeapon extends AbstractWeapon {

    public static final int MELEE_DAMAGE_MIN = 120;
    public static final int MELEE_DAMAGE_MAX = 180;
    public static final int CRIT_CHANCE_MIN = 12;
    public static final int CRIT_CHANCE_MAX = 20;
    public static final int CRIT_MULTIPLIER_MIN = 150;
    public static final int CRIT_MULTIPLIER_MAX = 200;
    public static final int HEALTH_BONUS_MIN = 200;
    public static final int HEALTH_BONUS_MAX = 500;
    public static final int SPEED_BONUS_MIN = 2;
    public static final int SPEED_BONUS_MAX = 8;
    protected int speedBonus;

    @Override
    public void generateStats() {
        this.meleeDamage = generateRandomValueBetween(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX);
        this.critChance = generateRandomValueBetween(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = generateRandomValueBetween(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = generateRandomValueBetween(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);

        this.speedBonus = generateRandomValueBetween(SPEED_BONUS_MIN, SPEED_BONUS_MAX);
    }
}
