package com.ebicep.warlords.database.repositories.player.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public interface SiegeStatsWarlordsClasses extends StatsWarlordsClasses<DatabaseGameSiege, DatabaseGamePlayerSiege, SiegeStats, StatsWarlordsSpecs<DatabaseGameSiege, DatabaseGamePlayerSiege, SiegeStats>>, SiegeStats {

    @Override
    default int getPointsCaptured() {
        return getStat(SiegeStats::getPointsCaptured, Integer::sum, 0);
    }

    @Override
    default int getPointsCapturedFail() {
        return getStat(SiegeStats::getPointsCapturedFail, Integer::sum, 0);
    }

    @Override
    default long getTimeOnPoint() {
        return getStat(SiegeStats::getTimeOnPoint, Long::sum, 0L);
    }

    @Override
    default int getPayloadsEscorted() {
        return getStat(SiegeStats::getPayloadsEscorted, Integer::sum, 0);
    }

    @Override
    default int getPayloadsEscortedFail() {
        return getStat(SiegeStats::getPayloadsEscortedFail, Integer::sum, 0);
    }

    @Override
    default int getPayloadsDefended() {
        return getStat(SiegeStats::getPayloadsDefended, Integer::sum, 0);
    }

    @Override
    default int getPayloadsDefendedFail() {
        return getStat(SiegeStats::getPayloadsDefendedFail, Integer::sum, 0);
    }

    @Override
    default long getTimeOnPayloadEscorting() {
        return getStat(SiegeStats::getTimeOnPayloadEscorting, Long::sum, 0L);
    }

    @Override
    default long getTimeOnPayloadDefending() {
        return getStat(SiegeStats::getTimeOnPayloadDefending, Long::sum, 0L);
    }

    @Override
    default long getTotalTimePlayed() {
        return getStat(SiegeStats::getTotalTimePlayed, Long::sum, 0L);
    }

}
