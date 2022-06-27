package com.ebicep.warlords.player;

import org.bukkit.ChatColor;

public enum WeaponsRarity {
    COMMON(ChatColor.GREEN),
    RARE(ChatColor.BLUE),
    EPIC(ChatColor.DARK_PURPLE),
    LEGENDARY(ChatColor.GOLD),
    ASCENDANT(ChatColor.RED)

    ;

    private final ChatColor weaponChatColor;

    WeaponsRarity(ChatColor weaponChatColor) {
        this.weaponChatColor = weaponChatColor;
    }

    public ChatColor getWeaponChatColor() {
        return weaponChatColor;
    }
}




