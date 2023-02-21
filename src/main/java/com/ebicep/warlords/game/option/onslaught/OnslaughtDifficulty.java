package com.ebicep.warlords.game.option.onslaught;

import org.bukkit.ChatColor;

public enum OnslaughtDifficulty {

    EASY(ChatColor.GREEN + "EASY"),
    MEDIUM(ChatColor.YELLOW + "MEDIUM"),
    HARD(ChatColor.GOLD + "HARD"),
    INSANE(ChatColor.RED + "INSANE"),
    EXTREME(ChatColor.DARK_RED + "EXTREME"),
    NIGHTMARE(ChatColor.LIGHT_PURPLE + "NIGHTMARE"),
    INSOMNIA(ChatColor.DARK_PURPLE + "INSOMNIA"),
    VANGUARD(ChatColor.DARK_GRAY + "VANGUARD"),
    MAX(ChatColor.BLACK.toString() + ChatColor.MAGIC + "?????")

    ;

    private final String name;

    OnslaughtDifficulty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
