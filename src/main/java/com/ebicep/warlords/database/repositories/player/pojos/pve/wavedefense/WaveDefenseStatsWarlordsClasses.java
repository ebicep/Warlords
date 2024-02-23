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
    default int getHighestWaveCleared() {
        return getStat(WaveDefenseStats::getHighestWaveCleared, Math::max, 0);
    }

    @Override
    default long getMostDamageInWave() {
        return getStat(WaveDefenseStats::getMostDamageInWave, Math::max, 0L);
    }

    @Override
    default long getFastestGameFinished() {
        return getStat(WaveDefenseStats::getFastestGameFinished, Math::min, Long.MAX_VALUE);
    }

}
