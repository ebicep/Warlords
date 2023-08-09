package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabasePlayerPvEOnslaughtDifficultyStats;
import com.ebicep.warlords.util.java.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.LEAD_5;
import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.UPPER_CENTER_1;

public class StatsLeaderboardOnslaught extends AbstractStatsLeaderboardGameType<DatabasePlayerPvEOnslaughtDifficultyStats> implements PvELeaderboard {

    private static final List<StatsLeaderboardCategory<DatabasePlayerPvEOnslaughtDifficultyStats>> CATEGORIES = new ArrayList<>() {{
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPveStats().getOnslaughtStats(), "All Modes", "All"));
    }};

    public StatsLeaderboardOnslaught() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "Onslaught";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabasePlayerPvEOnslaughtDifficultyStats> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();

        statsLeaderboards.add(new StatsLeaderboard("Average Time Lived",
                LEAD_5,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getAverageTimeLived(),
                databasePlayer -> StringUtils.formatTimeLeft(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getAverageTimeLived() / 20),
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getAverageTimeLived() == 0
        ));
        statsLeaderboards.add(new StatsLeaderboard("Longest Time Lived",
                UPPER_CENTER_1,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getLongestTicksLived(),
                databasePlayer -> StringUtils.formatTimeLeft(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getLongestTicksLived() / 20),
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getLongestTicksLived() == 0
        ));

    }

}
