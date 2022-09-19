package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weaponaddons.StarPieceBonus;
import com.ebicep.warlords.pve.weapons.weaponaddons.Upgradeable;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

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
        return Collections.singletonList(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalTenths(getSpeedBonus()) + "%" + getStarPieceBonusString(
                WeaponStats.SPEED_BONUS));
    }

    @Override
    public List<String> getLoreAddons() {
        return Collections.singletonList(ChatColor.LIGHT_PURPLE + "Upgrade Level [" + getUpgradeLevel() + "/" + getMaxUpgradeLevel() + "]");
    }

    @Override
    public List<WeaponStats> getRandomStatBonus() {
        List<WeaponStats> randomStatBonus = new ArrayList<>(super.getRandomStatBonus());
        randomStatBonus.add(WeaponStats.SPEED_BONUS);
        return randomStatBonus;
    }

    @Override
    public void upgrade() {
        this.upgradeLevel++;
        this.meleeDamage *= getUpgradeMultiplier();
        this.critChance *= getUpgradeMultiplier();
        this.critMultiplier *= getUpgradeMultiplier();
        this.healthBonus *= getUpgradeMultiplier();
        this.speedBonus *= getUpgradeMultiplier();
    }

    @Override
    public List<String> getUpgradeLore() {
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + NumberFormat.formatOptionalTenths(meleeDamage) + ChatColor.GRAY + " - " + ChatColor.RED + NumberFormat.formatOptionalHundredths(meleeDamage + getMeleeDamageRange()) + ChatColor.GREEN + " > " +
                        ChatColor.RED + NumberFormat.formatOptionalTenths(meleeDamage * getUpgradeMultiplier()) + ChatColor.GRAY + " - " + ChatColor.RED + NumberFormat.formatOptionalHundredths(meleeDamage * getUpgradeMultiplier() + getMeleeDamageRange()),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalTenths(critChance) + "%" + ChatColor.GREEN + " > " +
                        ChatColor.RED + NumberFormat.formatOptionalTenths(critChance * getUpgradeMultiplier()) + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalTenths(critMultiplier) + "%" + ChatColor.GREEN + " > " +
                        ChatColor.RED + NumberFormat.formatOptionalTenths(critMultiplier * getUpgradeMultiplier()) + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalTenths(healthBonus) + " > " +
                        "+" + NumberFormat.formatOptionalTenths(healthBonus * getUpgradeMultiplier()),
                ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalTenths(speedBonus) + "%" + " > " +
                        "+" + NumberFormat.formatOptionalTenths(speedBonus * getUpgradeMultiplier()) + "%"
        );
    }

    @Override
    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public float getSpeedBonus() {
        return starPieceBonus == WeaponStats.SPEED_BONUS ? speedBonus * getStarPieceBonusMultiplicativeValue() : speedBonus;
    }
}
