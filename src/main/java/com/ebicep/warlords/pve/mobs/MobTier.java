package com.ebicep.warlords.pve.mobs;

import org.bukkit.ChatColor;

public enum MobTier {

    BASE(ChatColor.YELLOW + "✻"),
    ELITE(ChatColor.GOLD.toString() + ChatColor.BOLD + "❈❈"),
    BOSS(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "✪✪✪"),
    RAID_BOSS(ChatColor.DARK_GRAY.toString() + ChatColor.BOLD + "❂❂❂❂")

    ;

    private final String symbol;

    MobTier(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

}
