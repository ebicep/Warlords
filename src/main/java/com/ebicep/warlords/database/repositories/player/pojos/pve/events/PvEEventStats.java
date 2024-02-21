package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStats;

public interface PvEEventStats<DatabaseGameT extends DatabaseGamePvEEvent, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEvent> extends PvEStats<DatabaseGameT, DatabaseGamePlayerT> {

    long getEventPointsCumulative();

    long getHighestEventPointsGame();

}
