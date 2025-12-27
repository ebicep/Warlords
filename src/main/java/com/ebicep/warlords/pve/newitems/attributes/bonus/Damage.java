package com.ebicep.warlords.pve.newitems.attributes.bonus;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.attributes.Attribute;
import com.ebicep.warlords.pve.newitems.attributes.NewItemCooldown;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Damage implements Attribute {

    @Override
    public String getDatabaseName() {
        return "DAMAGE";
    }

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Component formatValue(float value) {
        return ComponentBuilder
                .create()
                .text("+" + NumberFormat.formatOptionalTenths(value) + "% ", getTextColor())
                .text("Damage", NamedTextColor.GRAY)
                .build();
    }
    @Override
    public void apply(WarlordsPlayer warlordsPlayer, float value) {
        NewItemCooldown.giveCooldown(warlordsPlayer, cd -> cd.addDamageBoost(value));
    }

}
