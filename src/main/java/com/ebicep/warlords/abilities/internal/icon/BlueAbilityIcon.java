package com.ebicep.warlords.abilities.internal.icon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface BlueAbilityIcon extends AbilityIcon {

    ItemStack BLUE_ABILITY = new ItemStack(Material.LIME_DYE);

    @Override
    default ItemStack getAbilityIcon() {
        return BLUE_ABILITY;
    }

}
