package com.ebicep.warlords.database.leaderboards.stats.sections;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStats;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

/**
 * Different gamemodes
 * <p>ALL
 * <p>CTF
 */
public abstract class AbstractMultiStatsLeaderboardGameType<
        StatsWarlordsClassesT extends StatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGameBase<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        StatsT extends Stats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends StatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>,
        MultiStat extends MultiStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        CategoryT extends MultiStatsLeaderboardCategory<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT, MultiStat>>
        extends AbstractStatsLeaderboardGameType<DatabaseGameT, DatabaseGamePlayerT, MultiStat, CategoryT> {

    protected AbstractMultiStatsLeaderboardGameType(List<CategoryT> gameTypeCategories) {
        super(gameTypeCategories);
    }

    @Override
    public void addBaseLeaderboards(CategoryT statsLeaderboardCategory) {
        super.addBaseLeaderboards(statsLeaderboardCategory);
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();

        statsLeaderboards.add(new StatsLeaderboard("Experience",
                CENTER_BOARD,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getExperience())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Mage Experience",
                CENTER_BOARD_1,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClassExperience(Classes.MAGE),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                                                                                        .apply(databasePlayer)
                                                                                        .getClassExperience(Classes.MAGE))
        ));
        statsLeaderboards.add(new StatsLeaderboard("Warrior Experience",
                CENTER_BOARD_2,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClassExperience(Classes.WARRIOR),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                                                                                        .apply(databasePlayer)
                                                                                        .getClassExperience(Classes.WARRIOR))
        ));
        statsLeaderboards.add(new StatsLeaderboard("Paladin Experience",
                CENTER_BOARD_3,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClassExperience(Classes.PALADIN),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                                                                                        .apply(databasePlayer)
                                                                                        .getClassExperience(Classes.PALADIN))
        ));
        statsLeaderboards.add(new StatsLeaderboard("Shaman Experience",
                CENTER_BOARD_4,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClassExperience(Classes.SHAMAN),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                                                                                        .apply(databasePlayer)
                                                                                        .getClassExperience(Classes.SHAMAN))
        ));
        statsLeaderboards.add(new StatsLeaderboard("Rogue Experience",
                CENTER_BOARD_5,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClassExperience(Classes.ROGUE),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                                                                                        .apply(databasePlayer)
                                                                                        .getClassExperience(Classes.ROGUE))
        ));
        statsLeaderboards.add(new StatsLeaderboard("Arcanist Experience",
                CENTER_BOARD_6,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClassExperience(Classes.ARCANIST),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                                                                                        .apply(databasePlayer)
                                                                                        .getClassExperience(Classes.ARCANIST))
        ));
    }

}
