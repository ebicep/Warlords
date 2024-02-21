package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.PvEEventGardenOfHesperidesStats;

public interface PvEEventGardenOfHesperidesTartarusStats extends PvEEventGardenOfHesperidesStats<DatabaseGamePvEEventTartarus, DatabaseGamePlayerPvEEventTartarus> {

    long getFastestGameFinished();

}
