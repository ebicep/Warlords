package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;

import java.util.Set;

public interface PveOption {

    Game getGame();

    int playerCount();

    default int mobCount() {
        return (int) getMobs()
                .stream()
                .filter(mob -> mob.getWarlordsNPC().getTeam() == Team.RED)
                .count();
    }

    Set<AbstractMob<?>> getMobs();

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

    default boolean isPauseMobSpawn() {
        return false;
    }

    default void setPauseMobSpawn(boolean pauseMobSpawn) {
    }

}
