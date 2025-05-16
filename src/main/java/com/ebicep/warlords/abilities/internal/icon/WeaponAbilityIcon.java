package com.ebicep.warlords.abilities.internal.icon;

import com.ebicep.warlords.player.general.Weapons;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;

public interface WeaponAbilityIcon extends AbilityIcon {

    @Override
    default ItemStack getAbilityIcon() {
        return Weapons.STEEL_SWORD.getItem();
    }

    @Override
    default TextColor getAbilityColor() {
        return NamedTextColor.GREEN;
    }

}
