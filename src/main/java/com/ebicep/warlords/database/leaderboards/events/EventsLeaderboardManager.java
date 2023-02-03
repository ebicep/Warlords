package com.ebicep.warlords.database.leaderboards.events;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;

import java.util.HashMap;

public class EventsLeaderboardManager {

    public static final HashMap<EventLeaderboard, String> EVENT_LEADERBOARDS = new HashMap<>();

    public static void create() {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            return;
        }
        currentGameEvent.getEvent().addLeaderboards(currentGameEvent, EVENT_LEADERBOARDS);
        StatsLeaderboardManager.setLeaderboardHologramVisibilityToAll();
    }
}
