package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public interface PvEEventGardenOfHesperidesStatsWarlordsSpecs<
        DatabaseGameT extends DatabaseGamePvEEventGardenOfHesperides,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventGardenOfHesperides,
        T extends PvEEventGardenOfHesperidesStats<DatabaseGameT, DatabaseGamePlayerT>>
        extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T> {

}
