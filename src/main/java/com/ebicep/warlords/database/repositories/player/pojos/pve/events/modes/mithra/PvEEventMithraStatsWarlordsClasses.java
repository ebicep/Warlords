package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePlayerPvEEventMithra;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.DatabaseGamePvEEventMithra;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsClasses;

public interface PvEEventMithraStatsWarlordsClasses<DatabaseGameT extends DatabaseGamePvEEventMithra, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventMithra,
        T extends PvEEventMithraStats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends PvEEventMithraStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>>
        extends PvEEventStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, T, R> {

}
