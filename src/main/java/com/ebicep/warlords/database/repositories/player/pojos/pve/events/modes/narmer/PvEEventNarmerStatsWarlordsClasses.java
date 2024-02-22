package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsClasses;

public interface PvEEventNarmerStatsWarlordsClasses<DatabaseGameT extends DatabaseGamePvEEventNarmer<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventNarmer,
        T extends PvEEventNarmerStats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends PvEEventNarmerStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>>
        extends PvEEventStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, T, R> {

}
