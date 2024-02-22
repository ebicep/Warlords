package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public interface DuelStatsWarlordsClasses extends StatsWarlordsClasses<DatabaseGameDuel, DatabaseGamePlayerDuel, DuelStats, StatsWarlordsSpecs<DatabaseGameDuel, DatabaseGamePlayerDuel, DuelStats>>, DuelStats {

    @Override
    default long getTotalTimePlayed() {
        return getStat(DuelStats::getTotalTimePlayed, Long::sum, 0L);
    }

}
