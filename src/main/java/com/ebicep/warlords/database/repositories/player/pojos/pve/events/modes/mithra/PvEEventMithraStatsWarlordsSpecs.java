package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public interface PvEEventMithraStatsWarlordsSpecs<
        DatabaseGameT extends DatabaseGamePvEEventMithra,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventMithra,
        T extends PvEEventMithraStats<DatabaseGameT, DatabaseGamePlayerT>>
        extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T> {

}
