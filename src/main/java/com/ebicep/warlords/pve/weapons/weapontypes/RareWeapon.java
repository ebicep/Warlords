package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractTierTwoWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.java.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore.getAverageValue;

public class RareWeapon extends AbstractTierTwoWeapon implements Salvageable, WeaponScore {

    public static final int MELEE_DAMAGE_MIN = 100;
    public static final int MELEE_DAMAGE_MAX = 150;
    public static final int CRIT_CHANCE_MIN = 10;
    public static final int CRIT_CHANCE_MAX = 15;
    public static final int CRIT_MULTIPLIER_MIN = 140;
    public static final int CRIT_MULTIPLIER_MAX = 170;
    public static final int HEALTH_BONUS_MIN = 120;
    public static final int HEALTH_BONUS_MAX = 180;

    public RareWeapon() {
    }

    public RareWeapon(UUID uuid) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.RARE);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    public RareWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.RARE);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    @Override
    public WeaponsPvE getRarity() {
        return WeaponsPvE.RARE;
    }

    @Override
    public NamedTextColor getTextColor() {
        return NamedTextColor.BLUE;
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
        this.meleeDamage = JavaUtils.generateRandomValueBetweenInclusive(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX - getMeleeDamageRange());
        this.critChance = JavaUtils.generateRandomValueBetweenInclusive(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = JavaUtils.generateRandomValueBetweenInclusive(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = JavaUtils.generateRandomValueBetweenInclusive(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
    }

    @Override
    public int getMeleeDamageRange() {
        return 20;
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
        return 13;
    }

    @Override
    public int getMaxSalvageAmount() {
        return 15;
    }

}
