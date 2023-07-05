package com.ebicep.warlords.abilities.internal.icon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface AbilityIcon {

    ItemStack NO_ABILITY = new ItemStack(Material.BARRIER);

    default ItemStack getAbilityIcon() {
        return NO_ABILITY;
    }

}
