package com.ebicep.warlords.pve.newitems.attributes.basic;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class KnockbackResistance implements Attribute {

    @Override
    public String getDatabaseName() {
        return "KB_RES";
    }

    @Override
    public TextColor getTextColor() {
        return AbilityDescriptionBuilder.COLOR_BROWN;
    }

    @Override
    public Component formatValue(int value) {
        return ComponentBuilder
                .create()
                .text("+" + value + "% ", getTextColor())
                .text("Knockback Resistance", NamedTextColor.GRAY)
                .build();
    }
    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {

    }

}
