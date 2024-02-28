package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.MultiPvEEventNarmerStats;

public interface MultiPvEEventNarmerNarmersTombStats extends MultiPvEEventNarmerStats<
        PvEEventNarmerNarmersTombStatsWarlordsClasses,
        DatabaseGamePvEEventNarmersTomb,
        DatabaseGamePlayerPvEEventNarmersTomb,
        PvEEventNarmerNarmersTombStats,
        PvEEventNarmerNarmersTombStatsWarlordsSpecs>,
        PvEEventNarmerNarmersTombStats {

    @Override
    default int getHighestWaveCleared() {
        return getStat(PvEEventNarmerNarmersTombStats::getHighestWaveCleared, Math::max, 0);
    }

    @Override
    default int getTotalWavesCleared() {
        return getStat(PvEEventNarmerNarmersTombStats::getTotalWavesCleared, Integer::sum, 0);
    }

}
        