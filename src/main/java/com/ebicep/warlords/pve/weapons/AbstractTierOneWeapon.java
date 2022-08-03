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
    public List<String> getBaseStats() {
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(getMeleeDamageMin()) + " - " + NumberFormat.formatOptionalHundredths(getMeleeDamageMax()),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(getCritChance()) + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(getCritMultiplier()) + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+" + NumberFormat.formatOptionalHundredths(getHealthBonus())
        );
    }

    @Override
    public float getMeleeDamageMin() {
        return starPieceBonus == WeaponStats.MELEE_DAMAGE ? meleeDamage * getStarPieceBonusMultiplicativeValue() : meleeDamage;
    }

    @Override
    public float getMeleeDamageMax() {
        return starPieceBonus == WeaponStats.MELEE_DAMAGE ? (meleeDamage + getMeleeDamageRange()) * getStarPieceBonusMultiplicativeValue() : meleeDamage + getMeleeDamageRange();
    }

    @Override
    public float getCritChance() {
        return starPieceBonus == WeaponStats.CRIT_CHANCE ? critChance * getStarPieceBonusMultiplicativeValue() : critChance;
    }

    @Override
    public float getCritMultiplier() {
        return starPieceBonus == WeaponStats.CRIT_MULTIPLIER ? critMultiplier * getStarPieceBonusMultiplicativeValue() : critMultiplier;
    }

    @Override
    public float getHealthBonus() {
        return starPieceBonus == WeaponStats.HEALTH_BONUS ? healthBonus * getStarPieceBonusMultiplicativeValue() : healthBonus;
    }

    public WeaponStats getStarPieceBonus() {
        return starPieceBonus;
    }

    public void setStarPieceBonus() {
        this.starPieceBonus = generateRandomStatBonus();
    }
}
