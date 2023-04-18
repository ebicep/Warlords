package com.ebicep.warlords.pve;

import com.ebicep.warlords.game.GameMode;

public enum DifficultyMode {
    ANY("Any") {
        @Override
        public boolean validGameMode(GameMode gameMode) {
            return true;
        }
    },
    WAVE_DEFENSE("Wave Defense"),
    WAVE_DEFENSE_EASY(" - Easy") {
        @Override
        public boolean validDifficulty(DifficultyIndex difficultyIndex) {
            return difficultyIndex == DifficultyIndex.EASY;
        }

        @Override
        public String getShortName() {
            return "Easy";
        }
    },
    WAVE_DEFENSE_NORMAL(" - Normal") {
        @Override
        public boolean validDifficulty(DifficultyIndex difficultyIndex) {
            return difficultyIndex == DifficultyIndex.NORMAL;
        }

        @Override
        public String getShortName() {
            return "Normal";
        }
    },
    WAVE_DEFENSE_HARD(" - Hard") {
        @Override
        public boolean validDifficulty(DifficultyIndex difficultyIndex) {
            return difficultyIndex == DifficultyIndex.HARD;
        }

        @Override
        public String getShortName() {
            return "Hard";
        }
    },
    WAVE_DEFENSE_ENDLESS(" - Endless") {
        @Override
        public boolean validDifficulty(DifficultyIndex difficultyIndex) {
            return difficultyIndex == DifficultyIndex.ENDLESS;
        }

        @Override
        public String getShortName() {
            return "Endless";
        }
    },
    ONSLAUGHT("Onslaught") {
        @Override
        public boolean validGameMode(GameMode gameMode) {
            return gameMode == GameMode.ONSLAUGHT;
        }
    },
    EVENT("Event") {
        @Override
        public boolean validGameMode(GameMode gameMode) {
            return gameMode == GameMode.EVENT_WAVE_DEFENSE;
        }
    },

    ;

    public static final DifficultyMode[] VALUES = values();
    public final String name;

    DifficultyMode(String name) {
        this.name = name;
    }

    public String getShortName() {
        return name;
    }

    public DifficultyMode next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public boolean validGameMode(GameMode gameMode) {
        return gameMode == GameMode.WAVE_DEFENSE;
    }

    public boolean validDifficulty(DifficultyIndex difficultyIndex) {
        return false;
    }

}
