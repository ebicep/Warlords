package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.AbstractMob;

import java.util.Set;

public interface PveOption {

    Game getGame();

    int playerCount();

    int getWaveCounter();

    default DifficultyIndex getDifficulty() {
        return DifficultyIndex.NORMAL;
    }

    void spawnNewMob(AbstractMob<?> mob);

    Set<AbstractMob<?>> getMobs();
}
