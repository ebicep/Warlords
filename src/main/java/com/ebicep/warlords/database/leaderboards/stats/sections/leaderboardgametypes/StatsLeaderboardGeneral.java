package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerCompStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerPubStats;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

public class StatsLeaderboardGeneral extends AbstractStatsLeaderboardGameType<AbstractDatabaseStatInformation> {

    public StatsLeaderboardGeneral() {
        super(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer, ""), new StatsLeaderboardCategory<>(DatabasePlayer::getCompStats, "Comps"), new StatsLeaderboardCategory<>(DatabasePlayer::getPubStats, "Pubs"));
    }

    public void addLeaderboards() {
        addBaseLeaderboards(general);
        addBaseLeaderboards(comps);
        addBaseLeaderboards(pubs);
    }

    @Override
    public String getSubTitle() {
        return "All Modes";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<AbstractDatabaseStatInformation> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();

        Class<?> databasePlayerClass = statsLeaderboardCategory.statFunction.apply(new DatabasePlayer()).getClass();
        if (DatabasePlayer.class.equals(databasePlayerClass)) {
            statsLeaderboards.add(new StatsLeaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getMage().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getWarrior().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getPaladin().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getShaman().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayer.class).getRogue().getExperience())));
        } else if (DatabasePlayerCompStats.class.equals(databasePlayerClass)) {
            statsLeaderboards.add(new StatsLeaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getMage().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getWarrior().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getPaladin().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getShaman().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getRogue().getExperience())));
        } else if (DatabasePlayerPubStats.class.equals(databasePlayerClass)) {
            statsLeaderboards.add(new StatsLeaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getMage().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getWarrior().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getPaladin().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getShaman().getExperience())));
            statsLeaderboards.add(new StatsLeaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(statsLeaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getRogue().getExperience())));
        }
    }

    private <T> T applyAndCast(StatsLeaderboardCategory<AbstractDatabaseStatInformation> statsLeaderboardCategory, DatabasePlayer databasePlayer, Class<T> clazz) {
        return clazz.cast(statsLeaderboardCategory.statFunction.apply(databasePlayer));
    }

}
