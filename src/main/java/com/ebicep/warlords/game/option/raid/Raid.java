package com.ebicep.warlords.game.option.raid;

public enum Raid {

    THE_OBSIDIAN_TRAIL(
            "The Obsidian Trail",
            "A long time ago the Envoy king and queen of Illusion, Physira and Mithra reigned over the vanguard's legions." +
                    " During the old war Physira got corrupted by trying to stop an unknown obsidian force from taking his queen." +
                    " All that's left now is a trail of his ashes.",
            70
    ),
    GROUND_ZERO(
            "Ground Zero",
            "PLACEHOLDER",
            75
    ),
    THE_EVERGREEN_MANSION(
            "The Evergreen Mansion",
            "PLACEHOLDER",
            80
    ),
    SHADOWS_OF_THE_UNDERGROUND(
            "Shadows of the Underground",
            "PLACEHOLDER",
            85
    ),
    THE_STAIRWAY_OF_ILLUSION(
            "The Stairway of Illusion",
            "PLACEHOLDER",
            90
    ),
    THE_HALLS_OF_ASCENSION(
            "The Halls of Ascension",
            "PLACEHOLDER",
            95
    ),
    THRONE_OF_THE_CORRUPTED(
            "Throne of the Corrupted",
            "PLACEHOLDER",
            100
    ),

    ;

    public static final Raid[] VALUES = values();
    private final String name;
    private final String description;
    private final int minimumClassLevel;

    Raid(String name, String description, int minimumClassLevel) {
        this.name = name;
        this.description = description;
        this.minimumClassLevel = minimumClassLevel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinimumClassLevel() {
        return minimumClassLevel;
    }
}
