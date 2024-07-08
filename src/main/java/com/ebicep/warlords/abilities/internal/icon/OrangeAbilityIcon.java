package com.ebicep.warlords.abilities.internal.icon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface OrangeAbilityIcon extends AbilityIcon {

    ItemStack ORANGE_ABILITY = new ItemStack(Material.ORANGE_DYE);

    @Override
    default ItemStack getAbilityIcon() {
        return ORANGE_ABILITY;
    }

}
