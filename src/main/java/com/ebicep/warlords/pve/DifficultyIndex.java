package com.ebicep.warlords.pve;

import org.bukkit.ChatColor;

import javax.annotation.Nonnull;

public enum DifficultyIndex {

    EASY("Easy",
            """
                    For those seeking a lighter challenge,
                    recommended for solo players.

                    Modifiers:
                    §a-25% Mob Health
                    -25% Mob Damage
                    -25% Mob Spawns""",
            ChatColor.GREEN,
            25,
            .75f
    ),
    NORMAL("Normal",
            """
                    Fight off 25 waves of monsters to
                    earn rewards.

                    Modifiers:
                    §aNone""",
            ChatColor.YELLOW,
            25,
            1
    ),
    HARD("Hard",
            """
                    Fight off 25 waves of formidable
                    opponents and bosses with augmented
                    abilities.

                    Modifiers:
                    §c+50% Mob Health
                    +50% Mob Damage

                    Extreme scaling, Illusion, Exiled and
                    Void monsters appear much sooner and
                    at a higher rate.

                    No respawns, only way to respawn
                    is by clearing the wave.""",
            ChatColor.GOLD,
            25,
            2
    ),
    ENDLESS("Endless",
            """
                    Fight to the death against endless
                    waves of monsters to prove your
                    worth against the Vanguard.

                    Modifiers:
                    §c+25% Mob Spawns""",
            ChatColor.RED,
            Integer.MAX_VALUE,
            1.25f
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
