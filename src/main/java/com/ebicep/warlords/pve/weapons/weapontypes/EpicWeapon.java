package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractTierTwoWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.Salvageable;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.java.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

import static com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore.getAverageValue;
import static com.ebicep.warlords.util.java.NumberFormat.formatOptionalTenths;

public class EpicWeapon extends AbstractTierTwoWeapon implements Salvageable, WeaponScore, Upgradeable {

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
    @Field("speed_bonus")
    protected float speedBonus;
    @Field("upgrade_level")
    protected int upgradeLevel = 0;

    public EpicWeapon() {
    }

    public EpicWeapon(UUID uuid) {
        super(uuid);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.EPIC);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    public EpicWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
        this.selectedWeaponSkin = Weapons.getRandomWeaponFromRarity(WeaponsRarity.EPIC);
        this.unlockedWeaponSkins.add(this.selectedWeaponSkin);
    }

    @Override
    public void generateStats() {
        this.meleeDamage = Utils.generateRandomValueBetweenInclusive(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX - getMeleeDamageRange());
        this.critChance = Utils.generateRandomValueBetweenInclusive(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX);
        this.critMultiplier = Utils.generateRandomValueBetweenInclusive(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX);
        this.healthBonus = Utils.generateRandomValueBetweenInclusive(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX);
        this.speedBonus = Utils.generateRandomValueBetweenInclusive(SPEED_BONUS_MIN, SPEED_BONUS_MAX);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        player.getSpeed().addBaseModifier(getSpeedBonus());
    }

    @Override
    public WeaponsPvE getRarity() {
        return WeaponsPvE.EPIC;
    }

    @Override
    public int getMeleeDamageRange() {
        return 25;
    }

    @Override
    public List<Component> getLore() {
        return Arrays.asList(
                Component.text("Speed: ", NamedTextColor.GRAY)
                         .append(Component.text(format(getSpeedBonus()) + "%", NamedTextColor.GREEN)),
                Component.empty(),
                getWeaponScoreString()
        );
    }

    @Override
    public List<Component> getLoreAddons() {
        return Collections.singletonList(Component.text("Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]", NamedTextColor.LIGHT_PURPLE));
    }

    @Override
    public NamedTextColor getTextColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    public float getSpeedBonus() {
        return speedBonus;
    }

    @Override
    public void upgrade() {
        this.upgradeLevel++;
        this.meleeDamage *= meleeDamage < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.healthBonus *= healthBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.speedBonus *= speedBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
    }

    @Override
    public List<Component> getUpgradeLore() {
        float upgradedMeleeDamage = meleeDamage * (meleeDamage < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier());
        return Arrays.asList(
                Component.text("Damage: ", NamedTextColor.GRAY)
                         .append(Component.text(formatOptionalTenths(meleeDamage), NamedTextColor.RED))
                         .append(Component.text(" - "))
                         .append(Component.text(formatOptionalTenths(meleeDamage + getMeleeDamageRange()), NamedTextColor.RED))
                         .append(GREEN_ARROW)
                         .append(Component.text(formatOptionalTenths(upgradedMeleeDamage), NamedTextColor.RED))
                         .append(Component.text(" - "))
                         .append(Component.text(formatOptionalTenths(upgradedMeleeDamage + getMeleeDamageRange()), NamedTextColor.RED)),
                Component.text("Crit Chance: ", NamedTextColor.GRAY)
                         .append(Component.text(formatOptionalTenths(critChance) + "%", NamedTextColor.RED))
                         .append(GREEN_ARROW)
                         .append(Component.text(formatOptionalTenths(critChance) + "%", NamedTextColor.RED)),
                Component.text("Crit Multiplier: ", NamedTextColor.GRAY)
                         .append(Component.text(formatOptionalTenths(critMultiplier) + "%", NamedTextColor.RED))
                         .append(GREEN_ARROW)
                         .append(Component.text(formatOptionalTenths(critMultiplier) + "%", NamedTextColor.RED)),
                Component.empty(),
                Component.text("Health: ", NamedTextColor.GRAY)
                         .append(Component.text(format(healthBonus), NamedTextColor.GREEN))
                         .append(GREEN_ARROW)
                         .append(Component.text(format(healthBonus * (healthBonus > 0 ?
                                                                      getUpgradeMultiplier() :
                                                                      getUpgradeMultiplierNegative())), NamedTextColor.GREEN)),
                Component.text("Speed: ", NamedTextColor.GRAY)
                         .append(Component.text(format(speedBonus) + "%", NamedTextColor.GREEN))
                         .append(GREEN_ARROW)
                         .append(Component.text(format(speedBonus * (speedBonus > 0 ?
                                                                     getUpgradeMultiplier() :
                                                                     getUpgradeMultiplierNegative())) + "%", NamedTextColor.GREEN))
        );
    }

    @Override
    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    @Override
    public int getMaxUpgradeLevel() {
        return 2;
    }

    @Override
    public LinkedHashMap<Currencies, Long> getUpgradeCost(int tier) {
        LinkedHashMap<Currencies, Long> cost = new LinkedHashMap<>();
        if (tier == 1) {
            cost.put(Currencies.COIN, 10000L);
            cost.put(Currencies.SYNTHETIC_SHARD, 1000L);
        } else if (tier == 2) {
            cost.put(Currencies.COIN, 25000L);
            cost.put(Currencies.SYNTHETIC_SHARD, 2000L);
        }
        return cost;
    }

    @Override
    public List<Double> getWeaponScoreAverageValues() {
        return Arrays.asList(
                getAverageValue(MELEE_DAMAGE_MIN, MELEE_DAMAGE_MAX - getMeleeDamageRange(), meleeDamage / Math.pow(getUpgradeMultiplier(), upgradeLevel)),
                getAverageValue(CRIT_CHANCE_MIN, CRIT_CHANCE_MAX, critChance),
                getAverageValue(CRIT_MULTIPLIER_MIN, CRIT_MULTIPLIER_MAX, critMultiplier),
                getAverageValue(HEALTH_BONUS_MIN, HEALTH_BONUS_MAX, healthBonus / Math.pow(getUpgradeMultiplier(), upgradeLevel)),
                getAverageValue(SPEED_BONUS_MIN, SPEED_BONUS_MAX, speedBonus / Math.pow(getUpgradeMultiplier(), upgradeLevel))
        );
    }

    @Override
    public int getMinSalvageAmount() {
        return 53;
    }

    @Override
    public int getMaxSalvageAmount() {
        return 59;
    }
}
