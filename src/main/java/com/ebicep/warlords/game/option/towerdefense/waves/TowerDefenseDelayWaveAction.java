package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;

public class TowerDefenseDelayWaveAction implements WaveAction<TowerDefenseOption> {

    private final int delay;
    private int initialTicksElapsed = -1;

    public TowerDefenseDelayWaveAction(int delay) {
        this.delay = delay;
    }

    @Override
    public boolean tick(TowerDefenseOption pveOption) {
        if (initialTicksElapsed == -1) {
            initialTicksElapsed = pveOption.getTicksElapsed();
        }
        return pveOption.getTicksElapsed() - initialTicksElapsed >= delay;
    }
}
