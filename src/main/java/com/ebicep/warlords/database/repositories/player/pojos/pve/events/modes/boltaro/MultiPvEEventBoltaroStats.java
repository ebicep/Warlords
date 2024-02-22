package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.MultiPvEEventStats;

public interface MultiPvEEventBoltaroStats<
        StatsWarlordsClassesT extends PvEEventBoltaroStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGamePvEEventBoltaro<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventBoltaro,
        StatsT extends PvEEventBoltaroStats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends PvEEventBoltaroStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>>
        extends MultiPvEEventStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT> {

}
