package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePlayerPvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePvEEventIllumina;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.MultiPvEEventStats;

public interface MultiPvEEventIlluminaStats<
        StatsWarlordsClassesT extends PvEEventIlluminaStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGamePvEEventIllumina<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventIllumina,
        StatsT extends PvEEventIlluminaStats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends PvEEventIlluminaStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>>
        extends MultiPvEEventStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        PvEEventIlluminaStats<DatabaseGameT, DatabaseGamePlayerT> {

}
