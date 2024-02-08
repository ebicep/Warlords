package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.java.RandomCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Randomly spawns a mob based on its weight
 */
public class RandomWave implements TowerDefenseWave {

    private final List<WaveAction<TowerDefenseOption>> actions = new ArrayList<>();
    private int waveActionIndex = 0;
    private final RandomCollection<Mob> mobWeights = new RandomCollection<>();

    public RandomWave add(Mob mob, double weight) {
        mobWeights.add(weight, mob);
        return this;
    }

    /**
     * Always call this method last
     *
     * @param spawnAmount the amount of mobs to spawn
     * @return the wave
     */
    public RandomWave spawn(int spawnAmount) {
        for (int i = 0; i < spawnAmount; i++) {
            actions.add(new TowerDefenseSpawnWaveAction(mobWeights.next()));
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
