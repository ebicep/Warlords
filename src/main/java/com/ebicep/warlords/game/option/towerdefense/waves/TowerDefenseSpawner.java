package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class TowerDefenseSpawner {

    private final List<TowerDefenseWave> waves = new ArrayList<>();
    private final List<BukkitTask> activeWaves = new ArrayList<>();
    private int currentWave = 0; // index
    private TowerDefenseOption towerDefenseOption;
    private Game game;

    public void init(TowerDefenseOption towerDefenseOption) {
        this.towerDefenseOption = towerDefenseOption;
        this.game = towerDefenseOption.getGame();
        startCurrentWave();
    }

    public TowerDefenseSpawner add(TowerDefenseWave wave) {
        waves.add(wave);
        return this;
    }

    public void startCurrentWave() {
        startWave(currentWave, () -> {
            currentWave++;
            startCurrentWave();
        });
    }

    public void startWave(int waveIndex, Runnable onWaveComplete) {
        if (waveIndex >= waves.size()) {
            return;
        }
        TowerDefenseWave towerDefenseWave = waves.get(waveIndex);
        activeWaves.add(new GameRunnable(game) {
            @Override
            public void run() {
                if (towerDefenseWave.tick(towerDefenseOption)) {
                    onWaveComplete.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0));
    }

}
