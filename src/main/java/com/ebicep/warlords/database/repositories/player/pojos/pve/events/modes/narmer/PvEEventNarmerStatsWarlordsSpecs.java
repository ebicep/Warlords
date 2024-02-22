package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public interface PvEEventNarmerStatsWarlordsSpecs<
        DatabaseGameT extends DatabaseGamePvEEventNarmer,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventNarmer,
        T extends PvEEventNarmerStats<DatabaseGameT, DatabaseGamePlayerT>>
        extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T> {

}
