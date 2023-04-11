package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerGeneral;

import java.util.ArrayList;
import java.util.List;

public class StatsLeaderboardGeneral extends AbstractStatsLeaderboardGameType<DatabasePlayerGeneral> {

    private static final List<StatsLeaderboardCategory<DatabasePlayerGeneral>> CATEGORIES = new ArrayList<>() {{
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer, "All Queues", "All"));
        add(new StatsLeaderboardCategory<>(DatabasePlayer::getCompStats, "Competitive Queue", "Comps"));
        add(new StatsLeaderboardCategory<>(DatabasePlayer::getPubStats, "Public Queue", "Pubs"));
    }};


    public StatsLeaderboardGeneral() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "All Modes";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabasePlayerGeneral> statsLeaderboardCategory) {
    }
}

