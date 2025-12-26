package com.ebicep.warlords.pve.newitems.attributes.bonus;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class MaxEnergy implements Attribute {

    @Override
    public String getDatabaseName() {
        return "MAX_ENERGY";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.YELLOW;
    }

    @Override
    public Component formatValue(int value) {
        return ComponentBuilder
                .create()
                .text("+" + value + " ", getTextColor())
                .text("Max Energy", NamedTextColor.GRAY)
                .build();
    }
    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {

    }

}
