package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class for weapons that are above starter. Has StarPieceBonus
 */
public abstract class AbstractTierTwoWeapon extends AbstractTierOneWeapon {

    @Field("crit_chance_bonus")
    protected float critChance;
    @Field("crit_multiplier_bonus")
    protected float critMultiplier;

    public AbstractTierTwoWeapon() {
    }

    public AbstractTierTwoWeapon(UUID uuid) {
        super(uuid);
    }

    public AbstractTierTwoWeapon(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
    }

    @Override
    public List<String> getBaseStats() {
        return Arrays.asList(
                ChatColor.GRAY + "Damage: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getMeleeDamageMin()) + " - " +
                        NumberFormat.formatOptionalHundredths(getMeleeDamageMax()),
                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritChance()) + "%",
                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalTenths(getCritMultiplier()) + "%",
                "",
                ChatColor.GRAY + "Health: " + ChatColor.GREEN + format(getHealthBonus())
        );
    }

    @Override
    public float getMeleeDamageMin() {
        return meleeDamage;
    }

    @Override
    public float getMeleeDamageMax() {
        return meleeDamage + getMeleeDamageRange();
    }

    @Override
    public float getCritChance() {
        return critChance;
    }

    @Override
    public float getCritMultiplier() {
        return critMultiplier;
    }

    @Override
    public float getHealthBonus() {
        return healthBonus;
    }

}
