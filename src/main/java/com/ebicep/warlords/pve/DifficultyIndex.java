package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    EASY("Easy",
            "For those seeking a lighter challenge,\nrecommended for solo players." +
                    "\n\nModifiers:\n§a-25% Mob Health\n-25% Mob Damage\n-25% Mob Spawns",
            ChatColor.GREEN,
            25,
            24,
            2
    ),
    NORMAL("Normal",
            "Fight off 25 waves of monsters to\nearn rewards." +
                    "\n\nModifiers:\n§aNone",
            ChatColor.YELLOW,
            25,
            48,
            4
    ),
    HARD("Hard",
            "Fight off 25 waves of monsters to\nearn greater rewards." +
                    "\n\nModifiers:\n§c+25% Mob Health\n+25% Mob Damage\n\n- Tougher Mobs\n- No Respawns",
            ChatColor.GOLD,
            25,
            96,
            8
    ),
    ENDLESS("Endless",
            "Fight to the death against endless\nwaves of monsters to prove your\nworth against the Vanguard." +
                    "\n\nModifiers:\n§c+25% Mob Spawns",
            ChatColor.RED,
            10000,
            72,
            4
    );

    private final String name;
    private final String description;
    private final ChatColor difficultyColor;
    private final int maxWaves;
    private final int waveExperienceMultiplier;
    private final int waveGuildExperienceMultiplier;

    DifficultyIndex(
            @Nonnull String name,
            String description,
            ChatColor difficultyColor,
            int maxWaves,
            int waveExperienceMultiplier,
            int waveGuildExperienceMultiplier
    ) {
        this.name = name;
        this.description = description;
        this.difficultyColor = difficultyColor;
        this.maxWaves = maxWaves;
        this.waveExperienceMultiplier = waveExperienceMultiplier;
        this.waveGuildExperienceMultiplier = waveGuildExperienceMultiplier;
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

    public int getMaxWaves() {
        return maxWaves;
    }

    public int getWaveExperienceMultiplier() {
        return waveExperienceMultiplier;
    }

    public int getWaveGuildExperienceMultiplier() {
        return waveGuildExperienceMultiplier;
    }
}
