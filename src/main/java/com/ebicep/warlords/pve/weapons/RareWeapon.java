package com.ebicep.warlords.pve.weapons;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

public class RareWeapon extends AbstractWeapon {

    public static final int MELEE_DAMAGE_MIN = 100;
    @Transient
    public static final int MELEE_DAMAGE_MAX = 150;
    @Transient
    public static final int CRIT_CHANCE_MIN = 10;
    @Transient
    public static final int CRIT_CHANCE_MAX = 15;

    public static final int CRIT_MULTIPLIER_MIN = 140;
    @Transient
    public static final int CRIT_MULTIPLIER_MAX = 170;
    @Transient
    public static final int HEALTH_BONUS_MIN = 120;
    @Transient
    public static final int HEALTH_BONUS_MAX = 180;

    public RareWeapon() {
        super();
    }

    @Override
    public List<String> getLore() {
        return new ArrayList<>();
    }

    @Override
    public void generateStats() {
        this.meleeDamage = generateRandomValueBetween(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX);
        this.critChance = generateRandomValueBetween(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = generateRandomValueBetween(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = generateRandomValueBetween(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
    }
}
