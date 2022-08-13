package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.Leaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.LeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.LeaderboardGameType;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerCompStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerPubStats;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

public class LeaderboardGeneral extends LeaderboardGameType<AbstractDatabaseStatInformation> {

    public LeaderboardGeneral() {
        super(new LeaderboardCategory<>(databasePlayer -> databasePlayer, ""), new LeaderboardCategory<>(DatabasePlayer::getCompStats, "Comps"), new LeaderboardCategory<>(DatabasePlayer::getPubStats, "Pubs"));
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
    public void addExtraLeaderboards(LeaderboardCategory<AbstractDatabaseStatInformation> leaderboardCategory) {
        List<Leaderboard> leaderboards = leaderboardCategory.getLeaderboards();

        Class<?> databasePlayerClass = leaderboardCategory.statFunction.apply(new DatabasePlayer()).getClass();
        if (DatabasePlayer.class.equals(databasePlayerClass)) {
            leaderboards.add(new Leaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getMage().getExperience())));
            leaderboards.add(new Leaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getWarrior().getExperience())));
            leaderboards.add(new Leaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getPaladin().getExperience())));
            leaderboards.add(new Leaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getShaman().getExperience())));
            leaderboards.add(new Leaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getRogue().getExperience())));
        } else if (DatabasePlayerCompStats.class.equals(databasePlayerClass)) {
            leaderboards.add(new Leaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getMage().getExperience())));
            leaderboards.add(new Leaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getWarrior().getExperience())));
            leaderboards.add(new Leaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getPaladin().getExperience())));
            leaderboards.add(new Leaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getShaman().getExperience())));
            leaderboards.add(new Leaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getRogue().getExperience())));
        } else if (DatabasePlayerPubStats.class.equals(databasePlayerClass)) {
            leaderboards.add(new Leaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getMage().getExperience())));
            leaderboards.add(new Leaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getWarrior().getExperience())));
            leaderboards.add(new Leaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getPaladin().getExperience())));
            leaderboards.add(new Leaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getShaman().getExperience())));
            leaderboards.add(new Leaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getRogue().getExperience())));
        }
    }

    private <T> T applyAndCast(LeaderboardCategory<AbstractDatabaseStatInformation> leaderboardCategory, DatabasePlayer databasePlayer, Class<T> clazz) {
        return clazz.cast(leaderboardCategory.statFunction.apply(databasePlayer));
    }

}
