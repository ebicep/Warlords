package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
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

    public AbstractTierOneWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
    }

    @Override
    public List<String> getBaseStats() {
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getMeleeDamageMin()) + " - " + NumberFormat.formatOptionalHundredths(
                        getMeleeDamageMax()) + getStarPieceBonusString(WeaponStats.MELEE_DAMAGE),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritChance()) + "%" + getStarPieceBonusString(
                        WeaponStats.CRIT_CHANCE),
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritMultiplier()) + "%" + getStarPieceBonusString(
                        WeaponStats.CRIT_MULTIPLIER),
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + format(getHealthBonus()) + getStarPieceBonusString(WeaponStats.HEALTH_BONUS)
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

    public String getStarPieceBonusString(WeaponStats weaponStats) {
        return starPieceBonus == weaponStats ? getStarPieceBonusString() : "";
    }

    public WeaponStats getStarPieceBonus() {
        return starPieceBonus;
    }

    public void setStarPieceBonus() {
        this.starPieceBonus = generateRandomStatBonus();
    }
}
