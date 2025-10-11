package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.pve.MultiPvEStats;

public interface MultiPvEWaveDefenseStats extends MultiPvEStats<
        WaveDefenseStatsWarlordsClasses,
        DatabaseGamePvEWaveDefense,
        DatabaseGamePlayerPvEWaveDefense,
        WaveDefenseStats,
        WaveDefenseStatsWarlordsSpecs>,
        WaveDefenseStats {

    @Override
    default int getTotalWavesCleared() {
        return getStat(WaveDefenseStats::getTotalWavesCleared, Integer::sum, 0);
    }

    @Override
    default int getHighestWaveCleared() {
        return getStat(WaveDefenseStats::getHighestWaveCleared, Integer::max, 0);
    }

    @Override
    default long getMostDamageInWave() {
        return getStat(WaveDefenseStats::getMostDamageInWave, Long::max, 0L);
    }

    @Override
    default long getFastestGameFinished() {
        return getStat(WaveDefenseStats::getFastestGameFinished, (a, b) -> a == 0 ? b : b == 0 ? a : Math.min(a, b), 0L);
    }
}
