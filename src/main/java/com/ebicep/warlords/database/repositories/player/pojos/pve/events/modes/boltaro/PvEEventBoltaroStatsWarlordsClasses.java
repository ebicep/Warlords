package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsClasses;

public interface PvEEventBoltaroStatsWarlordsClasses<DatabaseGameT extends DatabaseGamePvEEventBoltaro, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventBoltaro,
        T extends PvEEventBoltaroStats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends PvEEventBoltaroStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>>
        extends PvEEventStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, T, R> {

}
