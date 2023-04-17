package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    EASY("Easy",
            "For those seeking a lighter challenge,\nrecommended for solo players." +
                    "\n\nModifiers:\n§a-25% Mob Health\n-25% Mob Damage\n-25% Mob Spawns",
            ChatColor.GREEN,
            25,
            .75f
    ),
    NORMAL("Normal",
            "Fight off 25 waves of monsters to\nearn rewards." +
                    "\n\nModifiers:\n§aNone",
            ChatColor.YELLOW,
            25,
            1
    ),
    HARD("Hard",
            "Fight off 25 waves of formidable\nopponents and bosses with augmented\nabilities." +
                    "\n\nModifiers:\n§c+50% Mob Health\n+50% Mob Damage\n\nExtreme scaling, Illusion, Exiled and\nVoid monsters appear much sooner and\nat a higher rate.\n\nNo respawns, only way to respawn\nis by clearing the wave.",
            ChatColor.GOLD,
            25,
            2
    ),
    ENDLESS("Endless",
            "Fight to the death against endless\nwaves of monsters to prove your\nworth against the Vanguard." +
                    "\n\nModifiers:\n§c+25% Mob Spawns",
            ChatColor.RED,
            Integer.MAX_VALUE,
            1.25f
    ),
    BOSS_RUSH("Boss Rush",
            "",
            ChatColor.RED,
            8,
            1
    ),
    EVENT("Event",
            "",
            ChatColor.BLUE,
            Integer.MAX_VALUE,
            1
    ),

    ;

    public static final DifficultyIndex[] NON_EVENT = new DifficultyIndex[]{
            EASY,
            NORMAL,
            HARD,
            ENDLESS
    };
    public static final DifficultyIndex[] VALUES = values();

    private final String name;
    private final String description;
    private final ChatColor difficultyColor;
    private final int maxWaves;
    private final float rewardsMultiplier;

    DifficultyIndex(
            @Nonnull String name,
            String description,
            ChatColor difficultyColor,
            int maxWaves,
            float rewardsMultiplier
    ) {
        this.name = name;
        this.description = description;
        this.difficultyColor = difficultyColor;
        this.maxWaves = maxWaves;
        this.rewardsMultiplier = rewardsMultiplier;
    }

    public DifficultyIndex next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
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

    public float getRewardsMultiplier() {
        return rewardsMultiplier;
    }
}
