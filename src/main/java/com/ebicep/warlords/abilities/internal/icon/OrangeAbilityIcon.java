package com.ebicep.warlords.abilities.internal.icon;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface OrangeAbilityIcon extends AbilityIcon {

    ItemStack ORANGE_ABILITY = new ItemStack(Material.ORANGE_DYE);

    @Override
    default ItemStack getAbilityIcon() {
        return ORANGE_ABILITY;
    }

    @Override
    default TextColor getAbilityColor() {
        return NamedTextColor.GOLD;
    }

}
