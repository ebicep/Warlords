package com.ebicep.warlords.pve;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;
import java.util.List;

public enum DifficultyIndex {

    EASY("Easy",
            List.of(
                    Component.text("For those seeking a lighter challenge,"),
                    Component.text("recommended for solo players."),
                    Component.empty(),
                    Component.text("Modifies:"),
                    Component.text("-25% Mob Health", NamedTextColor.GREEN),
                    Component.text("-25% Mob Damage", NamedTextColor.GREEN),
                    Component.text("-25% Mob Spawns", NamedTextColor.GREEN)
            ),
            NamedTextColor.GREEN,
            25,
            .75f
    ),
    NORMAL("Normal",
            List.of(
                    Component.text("Fight off 25 waves of monsters to"),
                    Component.text("earn rewards."),
                    Component.empty(),
                    Component.text("Modifies:"),
                    Component.text("None", NamedTextColor.GREEN)
            ),
            NamedTextColor.YELLOW,
            25,
            1
    ),
    HARD("Hard",
            List.of(
                    Component.text("Fight off 25 waves of formidable"),
                    Component.text("opponents and bosses with augmented"),
                    Component.text("abilities."),
                    Component.empty(),
                    Component.text("Modifiers:"),
                    Component.text("+50% Mob Health", NamedTextColor.RED),
                    Component.text("+50% Mob Damage", NamedTextColor.RED),
                    Component.empty(),
                    Component.text("Hard scaling, Illusion, Exiled and", NamedTextColor.RED),
                    Component.text("Void monsters appear much sooner and", NamedTextColor.RED),
                    Component.text("at a higher rate.", NamedTextColor.RED),
                    Component.empty(),
                    Component.text("No respawns, only way to respawn", NamedTextColor.RED),
                    Component.text("is by clearing the wave.", NamedTextColor.RED)
            ),
            NamedTextColor.GOLD,
            25,
            2
    ),
    EXTREME("Extreme",
            List.of(
                    Component.text("Fight off 25 waves of the hardest"),
                    Component.text("opponents and bosses with augmented"),
                    Component.text("abilities."),
                    Component.empty(),
                    Component.text("Modifiers:"),
                    Component.text("+100% Mob Health", NamedTextColor.RED),
                    Component.text("+75% Mob Damage", NamedTextColor.RED),
                    Component.empty(),
                    Component.text("Extreme Exiled and", NamedTextColor.RED),
                    Component.text("Void monsters appear much sooner and", NamedTextColor.RED),
                    Component.text("at a higher rate.", NamedTextColor.RED),
                    Component.empty(),
                    Component.text("No respawns, only way to respawn", NamedTextColor.RED),
                    Component.text("is by clearing the wave.", NamedTextColor.RED),
                    Component.empty(),
                    Component.text("Insignia gain is reduced.")
            ),
            NamedTextColor.DARK_RED,
            25,
            4
    ),
    ENDLESS("Endless",
            List.of(
                    Component.text("Fight to the death against endless"),
                    Component.text("waves of monsters to prove your"),
                    Component.text("worth against the Vanguard."),
                    Component.empty(),
                    Component.text("Modifies:"),
                    Component.text("+25% Mob Spawns", NamedTextColor.RED)
            ),
            NamedTextColor.RED,
            Integer.MAX_VALUE,
            1.25f
    ),
    BOSS_RUSH("Boss Rush",
            List.of(),
            NamedTextColor.RED,
            8,
            1
    ),
    EVENT("Event",
            List.of(),
            NamedTextColor.BLUE,
            Integer.MAX_VALUE,
            1
    ),

    ;

    public static final DifficultyIndex[] NON_EVENT = new DifficultyIndex[]{
            EASY,
            NORMAL,
            HARD,
            EXTREME,
            ENDLESS
    };
    public static final DifficultyIndex[] VALUES = values();

    private final String name;
    private final List<Component> description;
    private final NamedTextColor difficultyColor;
    private final int maxWaves;
    private final float rewardsMultiplier;

    DifficultyIndex(
            @Nonnull String name,
            List<Component> description,
            NamedTextColor difficultyColor,
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

    public NamedTextColor getDifficultyColor() {
        return difficultyColor;
    }

    public List<Component> getDescription() {
        return description;
    }

    public int getMaxWaves() {
        return maxWaves;
    }

    public float getRewardsMultiplier() {
        return rewardsMultiplier;
    }
}
