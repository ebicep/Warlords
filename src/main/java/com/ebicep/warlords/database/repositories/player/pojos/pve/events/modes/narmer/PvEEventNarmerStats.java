package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePlayerPvEEventNarmer;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.DatabaseGamePvEEventNarmer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStats;

public interface PvEEventNarmerStats<DatabaseGameT extends DatabaseGamePvEEventNarmer, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventNarmer>
        extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

}
