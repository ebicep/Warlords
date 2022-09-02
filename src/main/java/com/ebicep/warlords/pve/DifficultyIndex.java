package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    NORMAL("Normal", "Fight off 25 waves of monsters to\nearn rewards." +
            "\n\nModifiers:\n§aNone", ChatColor.YELLOW),
    HARD("Hard", "Fight off 50 waves of monsters to\nearn greater rewards." +
            "\n\nModifiers:\n§c+50% Mob Health\n+50% Mob Damage", ChatColor.GOLD),
    ENDLESS("Endless", "Fight to the death against endless\nwaves of monsters to prove your worth\nagainst the Vanguard." +
            "\n\nModifiers:\n§c+25% Mob Health\n+25% Mob Damage\n+50% Monster Spawns",  ChatColor.RED)

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