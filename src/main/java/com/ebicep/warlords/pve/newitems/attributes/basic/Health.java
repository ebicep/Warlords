package com.ebicep.warlords.pve.newitems.attributes.basic;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Health implements Attribute {

    @Override
    public String getDatabaseName() {
        return "HEALTH";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.DARK_RED;
    }

    @Override
    public Component formatValue(float value) {
        return ComponentBuilder
                .create()
                .text("✥ Health: ", NamedTextColor.GRAY)
                .text("+" + NumberFormat.formatOptionalTenths(value), NamedTextColor.DARK_RED)
                .build();
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value) {
        warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Item (Base)", value);
    }

}
