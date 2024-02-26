package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractMultiStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.MultiStatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.MultiPvEOnslaughtStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtStatsWarlordsSpecs;
import com.ebicep.warlords.util.java.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.LEAD_5;
import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.UPPER_CENTER_1;

class MultiStatsLeaderboardCategoryOnslaught extends MultiStatsLeaderboardCategory<OnslaughtStatsWarlordsClasses,
        DatabaseGamePvEOnslaught,
        DatabaseGamePlayerPvEOnslaught,
        OnslaughtStats,
        OnslaughtStatsWarlordsSpecs,
        MultiPvEOnslaughtStats> {

    public MultiStatsLeaderboardCategoryOnslaught(
            Function<DatabasePlayer, MultiPvEOnslaughtStats> databasePlayerMultiPvEOnslaughtStatsFunction,
            String categoryName,
            String shortName
    ) {
        super(databasePlayerMultiPvEOnslaughtStatsFunction, categoryName, shortName);
    }
}

public class StatsLeaderboardOnslaught extends AbstractMultiStatsLeaderboardGameType<
        OnslaughtStatsWarlordsClasses,
        DatabaseGamePvEOnslaught,
        DatabaseGamePlayerPvEOnslaught,
        OnslaughtStats,
        OnslaughtStatsWarlordsSpecs,
        MultiPvEOnslaughtStats,
        MultiStatsLeaderboardCategoryOnslaught>
        implements PvELeaderboard {

    private static final List<MultiStatsLeaderboardCategoryOnslaught> CATEGORIES = new ArrayList<>() {{
        add(new MultiStatsLeaderboardCategoryOnslaught(databasePlayer -> databasePlayer.getPveStats().getOnslaughtStats(), "All Modes", "All"));
    }};

    public StatsLeaderboardOnslaught() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "Onslaught";
    }

    @Override
    public void addExtraLeaderboards(MultiStatsLeaderboardCategoryOnslaught statsLeaderboardCategory) {
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
