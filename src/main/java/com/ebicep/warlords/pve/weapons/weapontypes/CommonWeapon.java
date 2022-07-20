package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ebicep.warlords.pve.weapons.weapontypes.WeaponScore.getAverageValue;

public class CommonWeapon extends AbstractWeapon implements Salvageable, WeaponScore, StatsRerollable {

    public static final int MELEE_DAMAGE_MIN = 80;
    public static final int MELEE_DAMAGE_MAX = 120;
    public static final int CRIT_CHANCE_MIN = 8;
    public static final int CRIT_CHANCE_MAX = 12;
    public static final int CRIT_MULTIPLIER_MIN = 125;
    public static final int CRIT_MULTIPLIER_MAX = 150;
    public static final int HEALTH_BONUS_MIN = 50;
    public static final int HEALTH_BONUS_MAX = 200;

    public CommonWeapon() {
        generateStats();
    }

    public CommonWeapon(UUID uuid) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.COMMON);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GREEN;
    }

    @Override
    public List<String> getLore() {
        return Arrays.asList(
                "",
                getWeaponScoreString()
        );
    }

    @Override
    public void generateStats() {
        this.meleeDamage = Utils.generateRandomValueBetweenInclusive(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX - getMeleeDamageRange());
        this.critChance = Utils.generateRandomValueBetweenInclusive(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = Utils.generateRandomValueBetweenInclusive(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = Utils.generateRandomValueBetweenInclusive(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
    }

    @Override
    public int getMeleeDamageRange() {
        return 15;
    }

    @Override
    public List<Double> getWeaponScoreAverageValues() {
        return Arrays.asList(
                getAverageValue(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX, meleeDamage + getMeleeDamageRange()),
                getAverageValue(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX, critChance),
                getAverageValue(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX, critMultiplier),
                getAverageValue(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX, healthBonus)
        );
    }

    @Override
    public int getMinSalvageAmount() {
        return 1;
    }

    @Override
    public int getMaxSalvageAmount() {
        return 2;
    }

    @Override
    public int getRerollCost() {
        return 100;
    }

    @Override
    public void reroll() {
        generateStats();
    }

    @Override
    public int getStarPieceBonusValue() {
        return 20;
    }

}
