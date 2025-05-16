package com.ebicep.warlords.abilities.internal.icon;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface AbilityIcon {

    ItemStack NO_ABILITY = new ItemStack(Material.BARRIER);

    default ItemStack getAbilityIcon() {
        return NO_ABILITY;
    }

    default TextColor getAbilityColor() {
        return NamedTextColor.GRAY;
    }

}
