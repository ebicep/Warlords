package com.ebicep.warlords.database.leaderboards.events;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroDifficultyStats;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Location;

import java.util.HashMap;

public class EventsLeaderboardManager {

    public static final HashMap<EventLeaderboard, String> EVENT_LEADERBOARDS = new HashMap<>();

    public static void create() {
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            return;
        }
        long eventStart = currentGameEvent.getStartDateSecond();
        //events
        //times
        //board types
        //boards
        EventLeaderboard lairBoard = new EventLeaderboard(
                eventStart,
                "Highest Game Event Points",
                new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 751.5),
                (databasePlayer, time) -> databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getLairStats()
                        .getHighestEventPointsGame(),
                (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getLairStats()
                        .getHighestEventPointsGame())
        );
        EventLeaderboard bonanzaBoard = new EventLeaderboard(
                eventStart,
                "Highest Game Event Points",
                new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 757.5),
                (databasePlayer, time) -> databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getBonanzaStats()
                        .getHighestEventPointsGame(),
                (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getBonanzaStats()
                        .getHighestEventPointsGame())
        );
        EventLeaderboard totalBoard = new EventLeaderboard(
                eventStart,
                "Event Points",
                new Location(StatsLeaderboardLocations.CENTER.getWorld(), -2539.5, 55, 737.5),
                (databasePlayer, time) -> databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getEventPointsCumulative(),
                (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getEventPointsCumulative())
        );
        EVENT_LEADERBOARDS.put(lairBoard, "Boltaro's Lair");
        EVENT_LEADERBOARDS.put(bonanzaBoard, "Boltaro Bonanza");
        EVENT_LEADERBOARDS.put(totalBoard, "Total Event Points");
        EVENT_LEADERBOARDS.forEach((eventLeaderboard, s) -> eventLeaderboard.resetHolograms(null, "", s));

        //GuildLeaderboardManager.resetEventBoards();
        StatsLeaderboardManager.setLeaderboardHologramVisibilityToAll();
    }
}
