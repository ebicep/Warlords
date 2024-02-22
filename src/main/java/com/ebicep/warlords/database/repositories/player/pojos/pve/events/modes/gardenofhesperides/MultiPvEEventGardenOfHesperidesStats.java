package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.MultiPvEEventStats;

public interface MultiPvEEventGardenOfHesperidesStats<
        StatsWarlordsClassesT extends PvEEventGardenOfHesperidesStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventGardenOfHesperides,
        StatsT extends PvEEventGardenOfHesperidesStats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>>
        extends MultiPvEEventStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT> {

}
