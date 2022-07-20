package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.pve.weapons.weapontypes.StarPieceBonus;
import com.ebicep.warlords.pve.weapons.weapontypes.Upgradeable;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public abstract class AbstractBetterWeapon extends AbstractWeapon implements StarPieceBonus, Upgradeable {

    @Field("speed_bonus")
    protected float speedBonus;
    @Field("upgrade_level")
    protected int upgradeLevel = 0;

    public AbstractBetterWeapon() {
    }

    public AbstractBetterWeapon(UUID uuid) {
        super(uuid);
    }

    @Override
    public List<String> getLore() {
        return Collections.singletonList(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + (starPieceBonus == WeaponStats.SPEED_BONUS ? getStarPieceBonusMultiplicativeString(speedBonus) + "%" + getStarPieceBonusString() : NumberFormat.formatOptionalHundredths(speedBonus) + "%"));
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
                ChatColor.GRAY + "Damage: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(meleeDamage) + ChatColor.GRAY + " - " + ChatColor.RED + NumberFormat.formatOptionalHundredths(meleeDamage + getMeleeDamageRange()) + ChatColor.GREEN + " > " +
                        ChatColor.RED + NumberFormat.formatOptionalHundredths(meleeDamage * getUpgradeMultiplier()) + ChatColor.GRAY + " - " + ChatColor.RED + NumberFormat.formatOptionalHundredths(meleeDamage * getUpgradeMultiplier() + getMeleeDamageRange()),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(critChance) + "%" + ChatColor.GREEN + " > " +
                        ChatColor.RED + NumberFormat.formatOptionalHundredths(critChance * getUpgradeMultiplier()) + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(critMultiplier) + "%" + ChatColor.GREEN + " > " +
                        ChatColor.RED + NumberFormat.formatOptionalHundredths(critMultiplier * getUpgradeMultiplier()) + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(healthBonus) + " > " +
                        "+" + NumberFormat.formatOptionalHundredths(healthBonus * getUpgradeMultiplier()),
                ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(speedBonus) + "%" + " > " +
                        "+" + NumberFormat.formatOptionalHundredths(speedBonus * getUpgradeMultiplier()) + "%"
        );
    }

    @Override
    public int getUpgradeLevel() {
        return upgradeLevel;
    }
}
