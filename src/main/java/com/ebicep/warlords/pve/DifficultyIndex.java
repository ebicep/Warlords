package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    NORMAL("Normal", "Fight off 25 waves of monsters to earn rewards." +
            "\n\nModifiers: §aNone", ChatColor.YELLOW),
    HARD("Normal", "Fight off 50 waves of monsters to earn greater rewards." +
            "\n\nModifiers:\n§c+50% Mob Health\n+50% Mob Damage", ChatColor.GOLD),
    ENDLESS("Endless", "Fight to the death against endless waves of" +
            "\nmonsters to prove your worth against the Vanguard.",  ChatColor.RED)

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
