package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    BEGINNER("Beginner", 0, ChatColor.GREEN),
    MEDIUM("Medium", 15, ChatColor.YELLOW),
    EXPERT("Expert", 30, ChatColor.GOLD),
    ENDLESS("Endless", 30, ChatColor.RED)

    ;

    private final String name;
    private final int requiredLevel;
    private final ChatColor difficultyColor;

    DifficultyIndex(@Nonnull String name, int requiredLevel, ChatColor difficultyColor) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.difficultyColor = difficultyColor;
    }

    public String getName() {
        return name;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public ChatColor getDifficultyColor() {
        return difficultyColor;
    }
}
