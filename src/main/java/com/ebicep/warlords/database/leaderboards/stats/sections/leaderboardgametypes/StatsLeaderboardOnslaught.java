package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabasePlayerPvEOnslaughtDifficultyStats;
import com.ebicep.warlords.util.warlords.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.LEAD_5;

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
                databasePlayer -> Utils.formatTimeLeft(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getAverageTimeLived() / 20),
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getAverageTimeLived() == 0
        ));

    }

}
