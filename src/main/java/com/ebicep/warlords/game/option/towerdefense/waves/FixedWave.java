package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Spawned a specific order of mobs
 */
public class FixedWave implements TowerDefenseWave {

    private final List<WaveAction<TowerDefenseOption>> actions = new ArrayList<>();
    private final List<WaveEndCondition> endConditions = new ArrayList<>();
    private int waveActionIndex = 0;

    public FixedWave add(Mob mob) {
        return add(mob, 1);
    }

    public FixedWave add(Mob mob, int amount) {
        return add(mob, amount, 0, null);
    }

    public FixedWave add(Mob mob, int amount, @Nullable WarlordsEntity spawner) {
        return add(mob, amount, 0, spawner);
    }

    public FixedWave add(Mob mob, int amount, int delay) {
        return add(mob, amount, delay, null);
    }

    public FixedWave add(Mob mob, int amount, int delay, @Nullable WarlordsEntity spawner) {
        for (int i = 0; i < amount; i++) {
            actions.add(new TowerDefenseDelayWaveAction(delay));
            actions.add(new TowerDefenseSpawnWaveAction(mob, spawner));
        }
        return this;
    }

    public FixedWave delay(int ticks) {
        actions.add(new TowerDefenseDelayWaveAction(ticks));
        return this;
    }

    public FixedWave addEndCondition(WaveEndCondition waveEndCondition) {
        endConditions.add(waveEndCondition);
        return this;
    }

    @Override
    public List<WaveAction<TowerDefenseOption>> getActions() {
        return actions;
    }

    @Override
    public List<WaveEndCondition> getEndConditions() {
        return endConditions;
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
