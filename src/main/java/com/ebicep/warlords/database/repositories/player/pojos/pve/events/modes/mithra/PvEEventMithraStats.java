package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStats;

public interface PvEEventMithraStats<DatabaseGameT extends DatabaseGamePvEEventMithra<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventMithra>
        extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

}
