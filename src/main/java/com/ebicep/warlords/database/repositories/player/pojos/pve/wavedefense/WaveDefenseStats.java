package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStats;

public interface WaveDefenseStats extends PvEStats<DatabaseGamePvEWaveDefense, DatabaseGamePlayerPvEWaveDefense> {

    int getTotalWavesCleared();

    int getHighestWaveCleared();

    long getMostDamageInWave();

    long getFastestGameFinished();

}
