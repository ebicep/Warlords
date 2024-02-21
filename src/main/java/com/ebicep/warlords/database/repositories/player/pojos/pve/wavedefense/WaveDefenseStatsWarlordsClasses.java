package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;


import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsClasses;

public interface WaveDefenseStatsWarlordsClasses extends PvEStatsWarlordsClasses<
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
    default int highestWaveCleared() {
        return getStat(WaveDefenseStats::highestWaveCleared, Math::max, 0);
    }

    @Override
    default long mostDamageInWave() {
        return getStat(WaveDefenseStats::mostDamageInWave, Math::max, 0L);
    }

    @Override
    default long fastestGameFinished() {
        return getStat(WaveDefenseStats::fastestGameFinished, Math::min, Long.MAX_VALUE);
    }

}
