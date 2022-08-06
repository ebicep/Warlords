package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    NORMAL("Normal", "Fight off 50 waves of monsters to earn rewards.", ChatColor.GOLD),
    ENDLESS("Endless", "Fight to the death against endless waves of\nmonsters to earn greater rewards.",  ChatColor.RED)

    ;

    private final String name;
    private final String description;
    private final ChatColor difficultyColor;

    DifficultyIndex(@Nonnull String name, String description, ChatColor difficultyColor) {
        this.name = name;
        this.description = description;
        this.difficultyColor = difficultyColor;
    }

    public String getName() {
        return name;
    }

    public ChatColor getDifficultyColor() {
        return difficultyColor;
    }

    public String getDescription() {
        return description;
    }
}
