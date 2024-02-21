package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStats;

public interface MultiPvEEventStats<
        StatsWarlordsClassesT extends PvEEventStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGamePvEEvent,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEvent,
        StatsT extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>>
        extends MultiStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT> {

}
