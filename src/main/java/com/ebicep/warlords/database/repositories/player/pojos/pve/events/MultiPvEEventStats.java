package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.MultiPvEStats;

public interface MultiPvEEventStats<
        StatsWarlordsClassesT extends PvEEventStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGamePvEEvent<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEvent,
        StatsT extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>>
        extends MultiPvEStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

    @Override
    default long getEventPointsCumulative() {
        return getStat(PvEEventStats::getEventPointsCumulative, Long::sum, 0L);
    }

    @Override
    default long getHighestEventPointsGame() {
        return getStat(PvEEventStats::getHighestEventPointsGame, Long::max, 0L);
    }

}
