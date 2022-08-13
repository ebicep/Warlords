package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.Leaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.LeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.LeaderboardGameType;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

public class LeaderboardCTF extends LeaderboardGameType<DatabasePlayerCTF> {

    public LeaderboardCTF() {
        super(
                new LeaderboardCategory<>(DatabasePlayer::getCtfStats, "All Queues"),
                new LeaderboardCategory<>(databasePlayer -> databasePlayer.getCompStats().getCtfStats(), "Comps"),
                new LeaderboardCategory<>(databasePlayer -> databasePlayer.getPubStats().getCtfStats(), "Pubs")
        );
    }

    public void addLeaderboards() {
        addBaseLeaderboards(general);
        addBaseLeaderboards(comps);
        addBaseLeaderboards(pubs);
    }

    @Override
    public String getSubTitle() {
        return "CTF";
    }

    @Override
    public void addExtraLeaderboards(LeaderboardCategory<DatabasePlayerCTF> leaderboardCategory) {
        List<Leaderboard> leaderboards = leaderboardCategory.getLeaderboards();
        leaderboards.add(new Leaderboard("Flags Captured", LEAD_5, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getFlagsCaptured(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getFlagsCaptured())));
        leaderboards.add(new Leaderboard("Flags Returned", CIRCULAR_1_OUTER_2, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getFlagsReturned(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getFlagsReturned())));

        leaderboards.add(new Leaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getMage().getExperience())));
        leaderboards.add(new Leaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getWarrior().getExperience())));
        leaderboards.add(new Leaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getPaladin().getExperience())));
        leaderboards.add(new Leaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getShaman().getExperience())));
        leaderboards.add(new Leaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getRogue().getExperience())));
    }

}
