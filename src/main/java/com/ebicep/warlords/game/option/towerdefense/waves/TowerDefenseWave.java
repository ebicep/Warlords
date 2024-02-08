package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;

import java.util.List;

/**
 * A wave is comprised of WaveActions. These actions can be spawning mobs, waiting a certain amount of time, etc
 */
public interface TowerDefenseWave {

    List<WaveAction<TowerDefenseOption>> getActions();

    default boolean tick(TowerDefenseOption towerDefenseOption) {
        List<WaveAction<TowerDefenseOption>> actions = getActions();
        int currentActionIndex = getWaveActionIndex();
        if (currentActionIndex < actions.size()) {
            WaveAction<TowerDefenseOption> currentAction = actions.get(currentActionIndex);
            if (currentAction.run(towerDefenseOption)) {
                setWaveActionIndex(currentActionIndex + 1);
            }
        }
        return waveComplete();
    }

    int getWaveActionIndex();

    void setWaveActionIndex(int index);

    default boolean waveComplete() {
        return getWaveActionIndex() >= getActions().size();
    }

}
