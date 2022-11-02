package com.ebicep.warlords.game.option.raid;

public enum Raid {

    THE_OBSIDIAN_TRAIL(
            "The Obsidian Trail",
            "PLACEHOLDER",
            30
    ),
    GROUND_ZERO(
            "Ground Zero",
            "PLACEHOLDER",
            40
    ),
    THE_EVERGREEN_MANSION(
            "The Evergreen Mansion",
            "PLACEHOLDER",
            50
    ),
    SHADOWS_OF_THE_UNDERGROUND(
            "Shadows of the Underground",
            "PLACEHOLDER",
            60
    ),
    THE_STAIRWAY_OF_ILLUSION(
            "The Stairway of Illusion",
            "PLACEHOLDER",
            70
    ),
    THE_HALLS_OF_ASCENSION(
            "The Halls of Ascension",
            "PLACEHOLDER",
            80
    ),
    THRONE_OF_THE_CORRUPTED(
            "Throne of the Corrupted",
            "PLACEHOLDER",
            90
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
