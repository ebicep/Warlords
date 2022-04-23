package com.ebicep.warlords.pve;

import javax.annotation.Nonnull;

public enum DifficultyIndex {
    BEGINNER("Beginner", 0, 0),
    MEDIUM("Medium", 15, 1200),
    EXPERT("Expert", 30, 2500),
    ENDLESS("Endless", 30, 2500)

    ;

    private final String name;
    private final int requiredLevel;
    private final int recommendedWeaponScore;

    DifficultyIndex(@Nonnull String name, int requiredLevel, int recommendedWeaponScore) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.recommendedWeaponScore = recommendedWeaponScore;
    }

    public String getName() {
        return name;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getRecommendedWeaponScore() {
        return recommendedWeaponScore;
    }
}
