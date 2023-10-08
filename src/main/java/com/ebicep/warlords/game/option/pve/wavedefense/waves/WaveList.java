package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import java.util.Random;

public interface WaveList {

    Wave getWave(int wave, Random random);

}
