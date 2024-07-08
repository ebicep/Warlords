package com.ebicep.warlords.abilities.internal.icon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface PurpleAbilityIcon extends AbilityIcon {

    ItemStack PURPLE_ABILITY = new ItemStack(Material.GLOWSTONE_DUST);

    @Override
    default ItemStack getAbilityIcon() {
        return PURPLE_ABILITY;
    }

}
