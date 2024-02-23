package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.CTFStats;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.CIRCULAR_1_OUTER_2;
import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.LEAD_5;

public class StatsLeaderboardCTF extends AbstractStatsLeaderboardGameType<DatabaseGameCTF, DatabaseGamePlayerCTF, CTFStats> {

    private static final List<StatsLeaderboardCategory<DatabaseGameCTF, DatabaseGamePlayerCTF, CTFStats>> CATEGORIES = new ArrayList<>() {{
//        add(new StatsLeaderboardCategory<>(DatabasePlayer::getCtfStats, "All Queues", "All"));
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getCompStats().getCtfStats(), "Competitive Queue", "Comps"));
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPubStats().getCtfStats(), "Public Queue", "Pubs"));
    }};

    public StatsLeaderboardCTF() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "CTF";
    }


    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabaseGameCTF, DatabaseGamePlayerCTF, CTFStats> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();
        statsLeaderboards.add(new StatsLeaderboard("Flags Captured", LEAD_5, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsCaptured(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsCaptured())));
        statsLeaderboards.add(new StatsLeaderboard("Flags Returned", CIRCULAR_1_OUTER_2, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsReturned(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsReturned())));
    }


}
