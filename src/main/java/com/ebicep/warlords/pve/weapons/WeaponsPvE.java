package com.ebicep.warlords.pve.weapons;

import org.bukkit.ChatColor;

public enum WeaponsPvE {

    COMMON_WEAPON(ChatColor.GREEN, "Common Weapon"),
    RARE_WEAPON(ChatColor.BLUE, "Rare Weapon"),
    EPIC_WEAPON(ChatColor.DARK_PURPLE, "Epic Weapon"),
    LEGENDARY_WEAPON(ChatColor.GOLD, "Legendary Weapon");

    public final ChatColor chatColor;
    public final String name;

    WeaponsPvE(ChatColor chatColor, String name) {
        this.chatColor = chatColor;
        this.name = name;
    }

    public String getGeneralName() {
        return chatColor + name;
    }

    public static WeaponsPvE getWeapon(AbstractWeapon abstractWeapon) {
        if (abstractWeapon instanceof CommonWeapon) {
            return COMMON_WEAPON;
        } else if (abstractWeapon instanceof RareWeapon) {
            return RARE_WEAPON;
        } else if (abstractWeapon instanceof EpicWeapon) {
            return EPIC_WEAPON;
        } else if (abstractWeapon instanceof LegendaryWeapon) {
            return LEGENDARY_WEAPON;
        } else {
            return null;
        }
    }
}
