package com.ebicep.warlords.game.option.wavedefense.waves;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class StaticWaveList implements WaveList {

    private final Map<Integer, Wave> waves = new HashMap<>();
    private static final Wave EMPTY_WAVE_LIST = new SimpleWave(0, 20, "No wave objects");
    private Wave lastWave = EMPTY_WAVE_LIST;

    public StaticWaveList add(int w, Wave wave) {
        waves.put(w, wave);
        return this;
    }

    public StaticWaveList loop(int loopAmount, int waveToLoop, int increment) {
        for (int i = 0; i < loopAmount; i++) {
            waves.put(waveToLoop + increment * i, waves.get(waveToLoop));
        }
        return this;
    }

    public StaticWaveList print() {
        waves.keySet().stream().sorted(Integer::compareTo).forEachOrdered(integer -> {
            System.out.println(integer + ": " + waves.get(integer).getMonsterCount());
        });
        return this;
    }

    @Override
    public Wave getWave(int wave, Random random) {
        lastWave = waves.getOrDefault(wave, lastWave);
        return lastWave;
    }

}
