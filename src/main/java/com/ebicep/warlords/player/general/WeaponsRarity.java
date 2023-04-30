package com.ebicep.warlords.player.general;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum WeaponsRarity {
    COMMON("Common", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.BLUE),
    EPIC("Epic", NamedTextColor.DARK_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD),
    ASCENDANT("Ascendant", NamedTextColor.RED);

    public final String name;
    private final NamedTextColor weaponChatColor;

    WeaponsRarity(String name, NamedTextColor weaponChatColor) {
        this.name = name;
        this.weaponChatColor = weaponChatColor;
    }

    public NamedTextColor getWeaponChatColor() {
        return weaponChatColor;
    }

    public Component coloredName() {
        return Component.text(name, weaponChatColor);
    }
}




