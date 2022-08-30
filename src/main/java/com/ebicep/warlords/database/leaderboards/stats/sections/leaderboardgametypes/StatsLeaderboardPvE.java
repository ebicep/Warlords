package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

public class StatsLeaderboardPvE extends AbstractStatsLeaderboardGameType<DatabasePlayerPvE> {

    public StatsLeaderboardPvE() {
        super(
                new StatsLeaderboardCategory<>(DatabasePlayer::getPveStats, "All Queues"),
                new StatsLeaderboardCategory<>(DatabasePlayer::getPveStats, "All Queues"),
                new StatsLeaderboardCategory<>(DatabasePlayer::getPveStats, "All Queues")
        );
    }

    @Override
    public String getSubTitle() {
        return "PvE";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabasePlayerPvE> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();
        statsLeaderboards.add(new StatsLeaderboard("Clear Rate", LEAD_5, databasePlayer -> statsLeaderboardCategory.statFunction.apply(databasePlayer).getWinRate(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.statFunction.apply(databasePlayer).getWinRate() * 100) + "%"));
        statsLeaderboards.add(new StatsLeaderboard("Waves Cleared", CIRCULAR_1_OUTER_2, databasePlayer -> statsLeaderboardCategory.statFunction.apply(databasePlayer).getTotalWavesCleared(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.statFunction.apply(databasePlayer).getTotalWavesCleared())));

        statsLeaderboards.add(new StatsLeaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> statsLeaderboardCategory.statFunction.apply(databasePlayer).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.statFunction.apply(databasePlayer).getMage().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> statsLeaderboardCategory.statFunction.apply(databasePlayer).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.statFunction.apply(databasePlayer).getWarrior().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> statsLeaderboardCategory.statFunction.apply(databasePlayer).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.statFunction.apply(databasePlayer).getPaladin().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> statsLeaderboardCategory.statFunction.apply(databasePlayer).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.statFunction.apply(databasePlayer).getShaman().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> statsLeaderboardCategory.statFunction.apply(databasePlayer).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.statFunction.apply(databasePlayer).getRogue().getExperience())));
    }

}
