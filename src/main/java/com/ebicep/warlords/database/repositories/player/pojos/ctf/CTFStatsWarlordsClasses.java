package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public interface CTFStatsWarlordsClasses extends StatsWarlordsClasses<DatabaseGameCTF, DatabaseGamePlayerCTF, CTFStats, StatsWarlordsSpecs<DatabaseGameCTF, DatabaseGamePlayerCTF, CTFStats>>, CTFStats {

    @Override
    default int getFlagsCaptured() {
        return getStat(CTFStats::getFlagsCaptured, Integer::sum, 0);
    }

    @Override
    default int getFlagsReturned() {
        return getStat(CTFStats::getFlagsReturned, Integer::sum, 0);
    }

    @Override
    default long getTotalBlocksTravelled() {
        return getStat(CTFStats::getTotalBlocksTravelled, Long::sum, 0L);
    }

    @Override
    default long getMostBlocksTravelled() {
        return getStat(CTFStats::getMostBlocksTravelled, Long::max, 0L);
    }

    @Override
    default long getTotalTimeInRespawn() {
        return getStat(CTFStats::getTotalTimeInRespawn, Long::sum, 0L);
    }

    @Override
    default long getTotalTimePlayed() {
        return getStat(CTFStats::getTotalTimePlayed, Long::sum, 0L);
    }

}
