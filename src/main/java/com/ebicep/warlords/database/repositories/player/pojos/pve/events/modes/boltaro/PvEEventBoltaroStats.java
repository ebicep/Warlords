package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePlayerPvEEventBoltaro;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStats;

public interface PvEEventBoltaroStats<DatabaseGameT extends DatabaseGamePvEEventBoltaro<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventBoltaro>
        extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

}
