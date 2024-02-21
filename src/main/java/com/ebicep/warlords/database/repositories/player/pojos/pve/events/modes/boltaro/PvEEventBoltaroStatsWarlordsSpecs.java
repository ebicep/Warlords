package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public interface PvEEventBoltaroStatsWarlordsSpecs<
        DatabaseGameT extends DatabaseGamePvEEventBoltaro,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventBoltaro,
        T extends PvEEventBoltaroStats<DatabaseGameT, DatabaseGamePlayerT>>
        extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T> {

}
