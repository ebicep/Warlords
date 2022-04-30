package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    NORMAL("Normal", ChatColor.GOLD),
    ENDLESS("Endless", ChatColor.RED)

    ;

    private final String name;
    private final ChatColor difficultyColor;

    DifficultyIndex(@Nonnull String name, ChatColor difficultyColor) {
        this.name = name;
        this.difficultyColor = difficultyColor;
    }

    public String getName() {
        return name;
    }

    public ChatColor getDifficultyColor() {
        return difficultyColor;
    }
}
