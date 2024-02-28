package com.ebicep.warlords.database.leaderboards.stats.sections;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStats;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

import java.util.function.Function;

/**
 * <p>ALL
 * <p>Comps
 * <p>Pubs
 */
public class MultiStatsLeaderboardCategory<
        StatsWarlordsClassesT extends StatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGameBase<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        StatsT extends Stats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends StatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>,
        MultiStat extends MultiStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>>
        extends StatsLeaderboardCategory<DatabaseGameT, DatabaseGamePlayerT, MultiStat> {

    public MultiStatsLeaderboardCategory(Function<DatabasePlayer, MultiStat> statFunction, String categoryName, String shortName) {
        super(statFunction, categoryName, shortName);
    }

}
