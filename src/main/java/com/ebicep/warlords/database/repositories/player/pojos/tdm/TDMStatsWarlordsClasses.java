package com.ebicep.warlords.database.repositories.player.pojos.tdm;


import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayerTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public interface TDMStatsWarlordsClasses extends StatsWarlordsClasses<DatabaseGameTDM, DatabaseGamePlayerTDM, TDMStats, StatsWarlordsSpecs<DatabaseGameTDM, DatabaseGamePlayerTDM, TDMStats>>, TDMStats {
    @Override
    default long getTotalTimePlayed() {
        return getStat(TDMStats::getTotalTimePlayed, Long::sum, 0L);
    }

}
