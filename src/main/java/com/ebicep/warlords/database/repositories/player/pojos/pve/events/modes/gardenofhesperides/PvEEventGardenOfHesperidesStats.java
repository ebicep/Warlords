package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStats;

public interface PvEEventGardenOfHesperidesStats<DatabaseGameT extends DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventGardenOfHesperides>
        extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

}
