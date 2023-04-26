package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
    public List<Component> getBaseStats() {
        return Arrays.asList(
                Component.text("Damage: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getMeleeDamageMin()) + " - " + NumberFormat.formatOptionalHundredths(
                                 getMeleeDamageMax()), NamedTextColor.RED)),
                Component.text("Crit Chance: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getCritChance()) + "%", NamedTextColor.RED)),
                Component.text("Crit Multiplier: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalTenths(getCritMultiplier()) + "%", NamedTextColor.RED)),
                Component.empty(),
                Component.text("Health: ", NamedTextColor.GRAY)
                         .append(Component.text(format(getHealthBonus()), NamedTextColor.GREEN))
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
