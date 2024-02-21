package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePlayerPvEEventIllumina;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.DatabaseGamePvEEventIllumina;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStats;

public interface PvEEventIlluminaStats<DatabaseGameT extends DatabaseGamePvEEventIllumina, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventIllumina>
        extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

}
