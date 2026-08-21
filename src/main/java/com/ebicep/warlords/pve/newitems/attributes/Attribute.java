package com.ebicep.warlords.pve.newitems.attributes;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

public interface Attribute {

    String getDatabaseName();

    String getName();

    TextColor getTextColor();

    ItemStack getItemStack();

    Component formatValue(float value, String prefix);

    void apply(WarlordsPlayer warlordsPlayer, float value);

}
