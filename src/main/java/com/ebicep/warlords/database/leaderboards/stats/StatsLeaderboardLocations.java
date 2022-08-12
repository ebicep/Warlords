package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;

public class StatsLeaderboardLocations {


    public static final Location LEADERBOARD_GAME_TYPE_SWITCH_LOCATION = new Location(LeaderboardManager.MAIN_LOBBY, -2558.5, 53, 719.5);
    public static final Location LEADERBOARD_CATEGORY_SWITCH_LOCATION = new Location(LeaderboardManager.MAIN_LOBBY, -2552.5, 53, 719.5);
    public static final Location LEADERBOARD_TIME_SWITCH_LOCATION = new Location(LeaderboardManager.MAIN_LOBBY, -2546.5, 53, 719.5);
    public static final Location CENTER = new LocationBuilder(LeaderboardManager.SPAWN_POINT.clone()).forward(.5f).left(21).addY(2);
}
