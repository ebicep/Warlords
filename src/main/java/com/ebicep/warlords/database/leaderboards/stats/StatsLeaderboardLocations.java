package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager.MAIN_LOBBY;

public class StatsLeaderboardLocations {

    public static final Location STATS_GAME_TYPE_SWITCH_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -2558.5, 53, 719.5);
    public static final Location STATS_CATEGORY_SWITCH_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -2552.5, 53, 719.5);
    public static final Location STATS_TIME_SWITCH_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -2546.5, 53, 719.5);
    public static final Location STATS_PAGE_SWITCH_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -2571.5, 54.5, 720.5);
    public static final Location CENTER = new LocationBuilder(StatsLeaderboardManager.SPAWN_POINT.clone()).forward(.5f).left(21).addY(2);
    //infront of spawn
    public static final Location CENTER_BOARD = new Location(MAIN_LOBBY, -2526.5, 57, 744.5);
    //left to right
    public static final Location CENTER_BOARD_1 = new Location(MAIN_LOBBY, -2523.5, 58, 734.5);
    public static final Location CENTER_BOARD_2 = new Location(MAIN_LOBBY, -2520.5, 58, 739.5);
    public static final Location CENTER_BOARD_3 = new Location(MAIN_LOBBY, -2516.5, 58, 744.5);
    public static final Location CENTER_BOARD_4 = new Location(MAIN_LOBBY, -2520.5, 58, 749.5);
    public static final Location CENTER_BOARD_5 = new Location(MAIN_LOBBY, -2523.5, 58, 754.5);
    //LEAD boards, left to right
    public static final Location LEAD_1 = new Location(MAIN_LOBBY, -2564.5, 56, 712.5);
    public static final Location LEAD_2 = new Location(MAIN_LOBBY, -2558.5, 56, 712.5);
    public static final Location LEAD_3 = new Location(MAIN_LOBBY, -2552.5, 56, 712.5);
    public static final Location LEAD_4 = new Location(MAIN_LOBBY, -2546.5, 56, 712.5);
    public static final Location LEAD_5 = new Location(MAIN_LOBBY, -2540.5, 56, 712.5);
    //circular room first floor (left to right)
    public static final Location CIRCULAR_1_CENTER = new Location(MAIN_LOBBY, -2608.5, 52, 728.5);
    public static final Location CIRCULAR_1_OUTER_1 = new Location(MAIN_LOBBY, -2600.5, 52, 733.5);
    public static final Location CIRCULAR_1_OUTER_2 = new Location(MAIN_LOBBY, -2608.5, 52, 737.5);
    public static final Location CIRCULAR_1_OUTER_3 = new Location(MAIN_LOBBY, -2616.5, 52, 733.5);
    public static final Location CIRCULAR_1_OUTER_4 = new Location(MAIN_LOBBY, -2616.5, 52, 723.5);
    public static final Location CIRCULAR_1_OUTER_5 = new Location(MAIN_LOBBY, -2608.5, 52, 719.5);
    public static final Location CIRCULAR_1_OUTER_6 = new Location(MAIN_LOBBY, -2600.5, 52, 723.5);
    //circular room second floor (left to right)
    public static final Location CIRCULAR_2_OUTER_1 = new Location(MAIN_LOBBY, -2608.5, 67, 738.5);
    public static final Location CIRCULAR_2_OUTER_2 = new Location(MAIN_LOBBY, -2619.5, 66.5, 735.5);
    public static final Location CIRCULAR_2_OUTER_3 = new Location(MAIN_LOBBY, -2619.5, 66.5, 721.5);
    public static final Location CIRCULAR_2_OUTER_4 = new Location(MAIN_LOBBY, -2608.5, 67, 719.5);

}
