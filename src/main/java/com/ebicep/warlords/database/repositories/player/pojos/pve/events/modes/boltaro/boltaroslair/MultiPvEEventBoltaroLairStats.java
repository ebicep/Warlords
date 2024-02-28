package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.MultiPvEEventBoltaroStats;

public interface MultiPvEEventBoltaroLairStats extends MultiPvEEventBoltaroStats<
        PvEEventBoltaroLairStatsWarlordsClasses,
        DatabaseGamePvEEventBoltaroLair,
        DatabaseGamePlayerPvEEventBoltarosLair,
        PvEEventBoltaroLairStats,
        PvEEventBoltaroLairStatsWarlordsSpecs>,
        PvEEventBoltaroLairStats {

    @Override
    default int getHighestWaveCleared() {
        return getStat(PvEEventBoltaroLairStats::getHighestWaveCleared, Math::max, 0);
    }

    @Override
    default int getTotalWavesCleared() {
        return getStat(PvEEventBoltaroLairStats::getTotalWavesCleared, Integer::sum, 0);
    }


}
