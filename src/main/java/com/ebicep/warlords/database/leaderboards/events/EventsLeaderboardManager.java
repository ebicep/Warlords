package com.ebicep.warlords.database.leaderboards.events;

import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroDifficultyStats;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;

public class EventsLeaderboardManager {

    public static final Set<EventLeaderboard> EVENT_LEADERBOARDS = new HashSet<>();

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
                        .getEventPointsCumulative(),
                (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getLairStats()
                        .getEventPointsCumulative())
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
                        .getEventPointsCumulative(),
                (databasePlayer, time) -> NumberFormat.addCommaAndRound(databasePlayer
                        .getPveStats()
                        .getEventStats()
                        .getBoltaroEventStats()
                        .getOrDefault(eventStart, new DatabasePlayerPvEEventBoltaroDifficultyStats())
                        .getBonanzaStats()
                        .getEventPointsCumulative())
        );
        EVENT_LEADERBOARDS.add(lairBoard);
        EVENT_LEADERBOARDS.add(bonanzaBoard);
        lairBoard.resetHolograms(null, "", "Boltaro's Lair");
        bonanzaBoard.resetHolograms(null, "", "Boltaro Bonanza");

        GuildLeaderboardManager.resetEventBoards();
        StatsLeaderboardManager.setLeaderboardHologramVisibilityToAll();
    }
}
