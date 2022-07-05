package com.ebicep.warlords.pve.weapons;

import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

public class CommonWeapon extends AbstractWeapon {

    @Transient
    public static final int MELEE_DAMAGE_MIN = 80;
    @Transient
    public static final int MELEE_DAMAGE_MAX = 120;
    @Transient
    public static final int CRIT_CHANCE_MIN = 8;
    @Transient
    public static final int CRIT_CHANCE_MAX = 12;
    @Transient
    public static final int CRIT_MULTIPLIER_MIN = 125;
    @Transient
    public static final int CRIT_MULTIPLIER_MAX = 150;
    @Transient
    public static final int HEALTH_BONUS_MIN = 50;
    @Transient
    public static final int HEALTH_BONUS_MAX = 200;

    public CommonWeapon() {
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
