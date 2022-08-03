package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.pve.weapons.weaponaddons.StarPieceBonus;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class for weapons that are above starter. Has StarPieceBonus
 */
public abstract class AbstractTierOneWeapon extends AbstractWeapon implements StarPieceBonus {

    @Field("crit_chance_bonus")
    protected float critChance;
    @Field("crit_multiplier_bonus")
    protected float critMultiplier;
    @Field("star_piece_bonus")
    protected WeaponStats starPieceBonus;

    public AbstractTierOneWeapon() {
    }

    public AbstractTierOneWeapon(UUID uuid) {
        super(uuid);
    }

    @Override
    protected List<String> getBaseStats() {
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + (starPieceBonus == WeaponStats.MELEE_DAMAGE ?
                        getStarPieceBonusMultiplicativeString(meleeDamage) + ChatColor.GRAY + " - " + ChatColor.RED + getStarPieceBonusMultiplicativeString(meleeDamage + getMeleeDamageRange()) + getStarPieceBonusString() :
                        NumberFormat.formatOptionalHundredths(meleeDamage) + ChatColor.GRAY + " - " + ChatColor.RED + NumberFormat.formatOptionalHundredths(meleeDamage + getMeleeDamageRange())),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + (starPieceBonus == WeaponStats.CRIT_CHANCE ? getStarPieceBonusMultiplicativeString(critChance) + "%" + getStarPieceBonusString() : NumberFormat.formatOptionalHundredths(critChance) + "%"),
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + (starPieceBonus == WeaponStats.CRIT_MULTIPLIER ? getStarPieceBonusMultiplicativeString(critMultiplier) + "%" + getStarPieceBonusString() : NumberFormat.formatOptionalHundredths(critMultiplier) + "%"),
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + (starPieceBonus == WeaponStats.HEALTH_BONUS ? getStarPieceBonusMultiplicativeString(healthBonus) + getStarPieceBonusString() : NumberFormat.formatOptionalHundredths(healthBonus))
        );
    }

    @Override
    public float getMeleeDamageMin() {
        float amount = starPieceBonus == WeaponStats.MELEE_DAMAGE ? meleeDamage * getStarPieceBonusMultiplicativeValue() : meleeDamage;
        return Math.round(amount);
    }

    @Override
    public float getMeleeDamageMax() {
        float amount = starPieceBonus == WeaponStats.MELEE_DAMAGE ? (meleeDamage + getMeleeDamageRange()) * getStarPieceBonusMultiplicativeValue() : meleeDamage + getMeleeDamageRange();
        return Math.round(amount);
    }

    @Override
    public float getCritChance() {
        float amount = starPieceBonus == WeaponStats.CRIT_CHANCE ? critChance * getStarPieceBonusMultiplicativeValue() : critChance;
        return Math.round(amount);
    }

    @Override
    public float getCritMultiplier() {
        float amount = starPieceBonus == WeaponStats.CRIT_MULTIPLIER ? critMultiplier * getStarPieceBonusMultiplicativeValue() : critMultiplier;
        return Math.round(amount);
    }

    @Override
    public float getHealthBonus() {
        float amount = starPieceBonus == WeaponStats.HEALTH_BONUS ? healthBonus * getStarPieceBonusMultiplicativeValue() : healthBonus;
        return Math.round(amount);
    }

    public WeaponStats getStarPieceBonus() {
        return starPieceBonus;
    }

    public void setStarPieceBonus() {
        this.starPieceBonus = generateRandomStatBonus();
    }
}
