package com.ebicep.warlords.pve.weapons;

import org.bukkit.ChatColor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collections;
import java.util.List;

public class EpicWeapon extends AbstractWeapon {

    public static final int MELEE_DAMAGE_MIN = 120;
    @Transient
    public static final int MELEE_DAMAGE_MAX = 180;
    @Transient
    public static final int CRIT_CHANCE_MIN = 12;
    @Transient
    public static final int CRIT_CHANCE_MAX = 20;
    @Transient
    public static final int CRIT_MULTIPLIER_MIN = 150;
    @Transient
    public static final int CRIT_MULTIPLIER_MAX = 200;
    @Transient
    public static final int HEALTH_BONUS_MIN = 200;
    @Transient
    public static final int HEALTH_BONUS_MAX = 500;
    @Transient
    public static final int SPEED_BONUS_MIN = 2;
    @Transient
    public static final int SPEED_BONUS_MAX = 8;
    @Field("speed_bonus")
    protected int speedBonus;

    public EpicWeapon() {
        super();
    }

    @Override
    public List<String> getLore() {
        return Collections.singletonList(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + speedBonus + "%");
    }

    @Override
    public void generateStats() {
        this.meleeDamage = generateRandomValueBetween(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX);
        this.critChance = generateRandomValueBetween(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = generateRandomValueBetween(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = generateRandomValueBetween(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);

        this.speedBonus = generateRandomValueBetween(SPEED_BONUS_MIN, SPEED_BONUS_MAX);
    }
}
