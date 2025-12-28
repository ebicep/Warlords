package com.ebicep.warlords.pve.newitems.attributes.bonus;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.pve.newitems.attributes.NewItemCooldown;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class CritChance implements Attribute {

    @Override
    public String getDatabaseName() {
        return "CC";
    }

    @Override
    public String getName() {
        return "Crit Chance";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }

    @Override
    public Component formatValue(float value, String prefix) {
        return ComponentBuilder
                .create()
                .text(prefix + NumberFormat.formatOptionalTenths(value) + "% ", getTextColor())
                .text(getName(), NamedTextColor.GRAY)
                .build();
    }
    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        NewItemCooldown.giveCooldown(warlordsPlayer, cd -> cd.addCritChance(value));
    }

}
