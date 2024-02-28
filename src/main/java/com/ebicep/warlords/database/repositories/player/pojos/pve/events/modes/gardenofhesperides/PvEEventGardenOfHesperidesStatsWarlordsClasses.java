package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePlayerPvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.DatabaseGamePvEEventGardenOfHesperides;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsClasses;

public interface PvEEventGardenOfHesperidesStatsWarlordsClasses<DatabaseGameT extends DatabaseGamePvEEventGardenOfHesperides<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventGardenOfHesperides,
        T extends PvEEventGardenOfHesperidesStats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends PvEEventGardenOfHesperidesStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>>
        extends PvEEventStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, T, R> {

}
