package com.ebicep.warlords.database.leaderboards.stats.sections.subsections;

import com.ebicep.warlords.database.leaderboards.stats.Leaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.LeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.LeaderboardGameType;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerCompStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayerPubStats;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Location;

import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.LeaderboardManager.MAIN_LOBBY;

public class LeaderboardGeneral extends LeaderboardGameType<AbstractDatabaseStatInformation> {

    public LeaderboardGeneral() {
        super(
                new LeaderboardCategory<>(databasePlayer -> databasePlayer),
                new LeaderboardCategory<>(DatabasePlayer::getCompStats),
                new LeaderboardCategory<>(DatabasePlayer::getPubStats)
        );
    }

    public void addLeaderboards() {
        addBaseLeaderboards(general);
        addBaseLeaderboards(comps);
        addBaseLeaderboards(pubs);
    }

    @Override
    public void addExtraLeaderboards(LeaderboardCategory<AbstractDatabaseStatInformation> leaderboardCategory) {
        List<Leaderboard> leaderboards = leaderboardCategory.getLeaderboards();

        Class<?> databasePlayerClass = leaderboardCategory.statFunction.apply(new DatabasePlayer()).getClass();
        if (DatabasePlayer.class.equals(databasePlayerClass)) {
            leaderboards.add(new Leaderboard("Mage Experience", new Location(MAIN_LOBBY, -2523.5, 58, 734.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getMage().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getMage().getExperience())));
            leaderboards.add(new Leaderboard("Warrior Experience", new Location(MAIN_LOBBY, -2520.5, 58, 739.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getWarrior().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getWarrior().getExperience())));
            leaderboards.add(new Leaderboard("Paladin Experience", new Location(MAIN_LOBBY, -2516.5, 58, 744.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getPaladin().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getPaladin().getExperience())));
            leaderboards.add(new Leaderboard("Shaman Experience", new Location(MAIN_LOBBY, -2520.5, 58, 749.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getShaman().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getShaman().getExperience())));
            leaderboards.add(new Leaderboard("Rogue Experience", new Location(MAIN_LOBBY, -2523.5, 58, 754.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getRogue().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayer.class).getRogue().getExperience())));
        } else if (DatabasePlayerCompStats.class.equals(databasePlayerClass)) {
            leaderboards.add(new Leaderboard("Mage Experience", new Location(MAIN_LOBBY, -2523.5, 58, 734.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getMage().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getMage().getExperience())));
            leaderboards.add(new Leaderboard("Warrior Experience", new Location(MAIN_LOBBY, -2520.5, 58, 739.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getWarrior().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getWarrior().getExperience())));
            leaderboards.add(new Leaderboard("Paladin Experience", new Location(MAIN_LOBBY, -2516.5, 58, 744.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getPaladin().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getPaladin().getExperience())));
            leaderboards.add(new Leaderboard("Shaman Experience", new Location(MAIN_LOBBY, -2520.5, 58, 749.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getShaman().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getShaman().getExperience())));
            leaderboards.add(new Leaderboard("Rogue Experience", new Location(MAIN_LOBBY, -2523.5, 58, 754.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getRogue().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerCompStats.class).getRogue().getExperience())));
        } else if (DatabasePlayerPubStats.class.equals(databasePlayerClass)) {
            leaderboards.add(new Leaderboard("Mage Experience", new Location(MAIN_LOBBY, -2523.5, 58, 734.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getMage().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getMage().getExperience())));
            leaderboards.add(new Leaderboard("Warrior Experience", new Location(MAIN_LOBBY, -2520.5, 58, 739.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getWarrior().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getWarrior().getExperience())));
            leaderboards.add(new Leaderboard("Paladin Experience", new Location(MAIN_LOBBY, -2516.5, 58, 744.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getPaladin().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getPaladin().getExperience())));
            leaderboards.add(new Leaderboard("Shaman Experience", new Location(MAIN_LOBBY, -2520.5, 58, 749.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getShaman().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getShaman().getExperience())));
            leaderboards.add(new Leaderboard("Rogue Experience", new Location(MAIN_LOBBY, -2523.5, 58, 754.5),
                    databasePlayer -> applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getRogue().getExperience(),
                    databasePlayer -> NumberFormat.addCommaAndRound(applyAndCast(leaderboardCategory, databasePlayer, DatabasePlayerPubStats.class).getRogue().getExperience())));
        }
    }

    private <T> T applyAndCast(LeaderboardCategory<AbstractDatabaseStatInformation> leaderboardCategory, DatabasePlayer databasePlayer, Class<T> clazz) {
        return clazz.cast(leaderboardCategory.statFunction.apply(databasePlayer));
    }
}
