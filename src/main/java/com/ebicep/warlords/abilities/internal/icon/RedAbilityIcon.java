package com.ebicep.warlords.abilities.internal.icon;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface RedAbilityIcon extends AbilityIcon {

    ItemStack RED_ABILITY = new ItemStack(Material.RED_DYE);

    @Override
    default ItemStack getAbilityIcon() {
        return RED_ABILITY;
    }

    @Override
    default TextColor getAbilityColor() {
        return NamedTextColor.RED;
    }

}
