package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStats;

public interface MultiPvEStats<
        StatsWarlordsClassesT extends PvEStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGamePvEBase,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEBase,
        StatsT extends PvEStats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends PvEStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>>
        extends MultiStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT> {

}
