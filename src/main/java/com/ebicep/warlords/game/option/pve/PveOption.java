package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;

import java.util.Set;

public interface PveOption {

    Game getGame();

    int playerCount();

    default int getWaveCounter() {
        return 1;
    }

    default DifficultyIndex getDifficulty() {
        return DifficultyIndex.NORMAL;
    }

    default void spawnNewMob(AbstractMob<?> mob) {
        spawnNewMob(mob, Team.RED);
    }

    void spawnNewMob(AbstractMob<?> mob, Team team);

    Set<AbstractMob<?>> getMobs();

    default boolean isPauseMobSpawn() {
        return false;
    }

    default void setPauseMobSpawn(boolean pauseMobSpawn) {
    }

}
