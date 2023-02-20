package com.ebicep.warlords.player.general;

import org.bukkit.ChatColor;

public enum WeaponsRarity {
    COMMON("Common", ChatColor.GREEN),
    RARE("Rare", ChatColor.BLUE),
    EPIC("Epic", ChatColor.DARK_PURPLE),
    LEGENDARY("Legendary", ChatColor.GOLD),
    ASCENDANT("Ascendant", ChatColor.RED);

    public final String name;
    private final ChatColor weaponChatColor;

    WeaponsRarity(String name, ChatColor weaponChatColor) {
        this.name = name;
        this.weaponChatColor = weaponChatColor;
    }

    public ChatColor getWeaponChatColor() {
        return weaponChatColor;
    }

    public String coloredName() {
        return weaponChatColor + name;
    }
}




