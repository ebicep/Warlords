package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;


import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

import java.util.ArrayList;
import java.util.List;

public class StatsLeaderboardGeneral extends AbstractStatsLeaderboardGameType<
        DatabaseGameBase<DatabaseGamePlayerBase>,
        DatabaseGamePlayerBase,
        Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>,
        StatsLeaderboardCategory<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>>> {

    private static final List<StatsLeaderboardCategory<DatabaseGameBase<DatabaseGamePlayerBase>,
            DatabaseGamePlayerBase,
            Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>>> CATEGORIES = new ArrayList<>() {{
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer, "All Queues", "All", true));
        add(new StatsLeaderboardCategory<>(DatabasePlayer::getCompStats, "Competitive Queue", "Comps", true));
        add(new StatsLeaderboardCategory<>(DatabasePlayer::getPubStats, "Public Queue", "Pubs", true));
    }};


    public StatsLeaderboardGeneral() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "All Modes";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase, Stats<DatabaseGameBase<DatabaseGamePlayerBase>, DatabaseGamePlayerBase>> statsLeaderboardCategory) {

    }

}

