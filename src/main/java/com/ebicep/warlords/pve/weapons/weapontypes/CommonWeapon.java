package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractTierTwoWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.java.MathUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore.getAverageValue;

public class CommonWeapon extends AbstractTierTwoWeapon implements Salvageable, WeaponScore {

    public static final int MELEE_DAMAGE_MIN = 80;
    public static final int MELEE_DAMAGE_MAX = 120;
    public static final int CRIT_CHANCE_MIN = 8;
    public static final int CRIT_CHANCE_MAX = 12;
    public static final int CRIT_MULTIPLIER_MIN = 125;
    public static final int CRIT_MULTIPLIER_MAX = 150;
    public static final int HEALTH_BONUS_MIN = 50;
    public static final int HEALTH_BONUS_MAX = 200;

    public CommonWeapon() {
    }

    public CommonWeapon(UUID uuid) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.COMMON);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    public CommonWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.COMMON);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    @Override
    public WeaponsPvE getRarity() {
        return WeaponsPvE.COMMON;
    }

    @Override
    public NamedTextColor getTextColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.empty(),
                getWeaponScoreString()
        );
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MathUtils.generateRandomValueBetweenInclusive(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX - getMeleeDamageRange());
        this.critChance = MathUtils.generateRandomValueBetweenInclusive(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = MathUtils.generateRandomValueBetweenInclusive(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = MathUtils.generateRandomValueBetweenInclusive(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
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
        return 5;
    }

    @Override
    public int getMaxSalvageAmount() {
        return 7;
    }

}
