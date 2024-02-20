package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStats;

import java.util.Map;

public interface PvEEventStats extends PvEStats<DatabaseGamePvEEvent, DatabaseGamePlayerPvEEvent> {

    Map<String, Long> getMobKills();

    Map<String, Long> getMobAssists();

    Map<String, Long> getMobDeaths();

    long getEventPointsCumulative();

    long getHighestEventPointsGame();

}
