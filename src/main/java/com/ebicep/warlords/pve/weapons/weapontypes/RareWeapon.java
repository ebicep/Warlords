package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.java.Utils;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

public class RareWeapon extends AbstractWeapon implements Salvageable {

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
        this.meleeDamage = Utils.generateRandomValueBetweenInclusive(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX);
        this.critChance = Utils.generateRandomValueBetweenInclusive(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = Utils.generateRandomValueBetweenInclusive(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = Utils.generateRandomValueBetweenInclusive(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
    }

    @Override
    public int getMinSalvageAmount() {
        return 3;
    }

    @Override
    public int getMaxSalvageAmount() {
        return 6;
    }
}
