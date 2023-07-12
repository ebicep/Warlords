package com.ebicep.warlords.abilities.internal.icon;

import com.ebicep.warlords.player.general.Weapons;
import org.bukkit.inventory.ItemStack;

public interface WeaponAbilityIcon extends AbilityIcon {

    @Override
    default ItemStack getAbilityIcon() {
        return Weapons.STEEL_SWORD.getItem();
    }

}
