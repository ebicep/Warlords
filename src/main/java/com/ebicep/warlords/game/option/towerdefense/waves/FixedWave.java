package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.pve.mobs.Mob;

import java.util.ArrayList;
import java.util.List;

/**
 * Spawned a specific order of mobs
 */
public class FixedWave implements TowerDefenseWave {

    private final List<WaveAction<TowerDefenseOption>> actions = new ArrayList<>();
    private int waveActionIndex = 0;

    public FixedWave add(Mob mob) {
        return add(mob, 1);
    }

    public FixedWave add(Mob mob, int amount) {
        for (int i = 0; i < amount; i++) {
            actions.add(new TowerDefenseSpawnWaveAction(mob));
        }
        return this;
    }

    @Override
    public List<WaveAction<TowerDefenseOption>> getActions() {
        return actions;
    }

    @Override
    public int getWaveActionIndex() {
        return waveActionIndex;
    }

    @Override
    public void setWaveActionIndex(int index) {
        this.waveActionIndex = index;
    }
}
