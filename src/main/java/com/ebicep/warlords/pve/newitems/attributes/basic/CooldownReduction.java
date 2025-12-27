package com.ebicep.warlords.pve.newitems.attributes.basic;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class CooldownReduction implements Attribute {

    @Override
    public String getDatabaseName() {
        return "CDR";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.AQUA;
    }

    @Override
    public Component formatValue(float value) {
        return ComponentBuilder
                .create()
                .text("+" + NumberFormat.formatOptionalTenths(value) + "% ", getTextColor())
                .text("Cooldown Reduction", NamedTextColor.GRAY)
                .build();
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
        float calculatedValue = 1 - value / 100f;
        for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
            ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Item", calculatedValue);
        }
    }

}
