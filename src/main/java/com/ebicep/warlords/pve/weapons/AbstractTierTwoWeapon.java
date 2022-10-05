package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weaponaddons.StarPieceBonus;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

import static com.ebicep.warlords.util.java.NumberFormat.formatOptionalTenths;

/**
 * Abstract class for weapons that are above starter/common/rare
 */
public abstract class AbstractTierTwoWeapon extends AbstractTierOneWeapon implements StarPieceBonus, Upgradeable {

    @Field("speed_bonus")
    protected float speedBonus;
    @Field("upgrade_level")
    protected int upgradeLevel = 0;

    public AbstractTierTwoWeapon() {
    }

    public AbstractTierTwoWeapon(UUID uuid) {
        super(uuid);
    }

    public AbstractTierTwoWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        player.getSpeed().addBaseModifier(getSpeedBonus());
    }

    @Override
    public List<String> getLore() {
        return Collections.singletonList(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + format(getSpeedBonus()) + "%" + getStarPieceBonusString(
                WeaponStats.SPEED_BONUS));
    }

    @Override
    public List<String> getLoreAddons() {
        return Collections.singletonList(ChatColor.LIGHT_PURPLE + "Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]");
    }

    public float getSpeedBonus() {
        return starPieceBonus == WeaponStats.SPEED_BONUS ? speedBonus * getStarPieceBonusMultiplicativeValue() : speedBonus;
    }

    @Override
    public List<WeaponStats> getRandomStatBonus() {
        List<WeaponStats> randomStatBonus = new ArrayList<>(super.getRandomStatBonus());
        if (speedBonus > 0) {
            randomStatBonus.add(WeaponStats.SPEED_BONUS);
        }
        return randomStatBonus;
    }

    @Override
    public void upgrade() {
        this.upgradeLevel++;
        this.meleeDamage *= meleeDamage < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.critChance *= critChance < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.critMultiplier *= critMultiplier < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.healthBonus *= healthBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
        this.speedBonus *= speedBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier();
    }

    @Override
    public List<String> getUpgradeLore() {
        float upgradedMeleeDamage = meleeDamage * (meleeDamage < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier());
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED +
                        formatOptionalTenths(meleeDamage) + ChatColor.GRAY + " - " + ChatColor.RED + formatOptionalTenths(meleeDamage + getMeleeDamageRange()) +
                        ChatColor.GREEN + " > " +
                        ChatColor.RED + formatOptionalTenths(upgradedMeleeDamage) + ChatColor.GRAY + " - " + ChatColor.RED + formatOptionalTenths(
                        upgradedMeleeDamage + getMeleeDamageRange()),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + formatOptionalTenths(critChance) + "%" + ChatColor.GREEN + " > " +
                        ChatColor.RED + formatOptionalTenths(critChance * (critChance < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())) + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + formatOptionalTenths(critMultiplier) + "%" + ChatColor.GREEN + " > " +
                        ChatColor.RED + formatOptionalTenths(critMultiplier * (critMultiplier < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())) + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + format(healthBonus) + " > " +
                        format(healthBonus * (healthBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())),
                ChatColor.GRAY + "Speed: " + ChatColor.GREEN + format(speedBonus) + "%" + " > " +
                        format(speedBonus * (speedBonus < 0 ? getUpgradeMultiplierNegative() : getUpgradeMultiplier())) + "%"
        );
    }

    @Override
    public int getUpgradeLevel() {
        return upgradeLevel;
    }
}
