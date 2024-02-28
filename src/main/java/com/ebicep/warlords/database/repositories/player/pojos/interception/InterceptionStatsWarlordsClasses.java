package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayerInterception;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public interface InterceptionStatsWarlordsClasses extends StatsWarlordsClasses<DatabaseGameInterception, DatabaseGamePlayerInterception, InterceptionStats, StatsWarlordsSpecs<DatabaseGameInterception, DatabaseGamePlayerInterception, InterceptionStats>>, InterceptionStats {

    @Override
    default int getPointsCaptured() {
        return getStat(InterceptionStats::getPointsCaptured, Integer::sum, 0);
    }

    @Override
    default int getPointsDefended() {
        return getStat(InterceptionStats::getPointsDefended, Integer::sum, 0);
    }


    @Override
    default long getTotalTimePlayed() {
        return getStat(InterceptionStats::getTotalTimePlayed, Long::sum, 0L);
    }

}
