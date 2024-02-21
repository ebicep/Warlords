package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.pve.MultiPvEStats;

public interface MultiPvEWaveDefenseStats extends MultiPvEStats<
        WaveDefenseStatsWarlordsClasses,
        DatabaseGamePvEWaveDefense,
        DatabaseGamePlayerPvEWaveDefense,
        WaveDefenseStats,
        WaveDefenseStatsWarlordsSpecs> {


}
