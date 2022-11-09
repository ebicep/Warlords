package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

public class StatsLeaderboardCTF extends AbstractStatsLeaderboardGameType<DatabasePlayerCTF> {

    private static final List<StatsLeaderboardCategory<DatabasePlayerCTF>> CATEGORIES = new ArrayList<>() {{
        add(new StatsLeaderboardCategory<>(DatabasePlayer::getCtfStats, "All Queues", "All"));
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
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabasePlayerCTF> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();
        statsLeaderboards.add(new StatsLeaderboard("Flags Captured", LEAD_5, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsCaptured(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsCaptured())));
        statsLeaderboards.add(new StatsLeaderboard("Flags Returned", CIRCULAR_1_OUTER_2, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsReturned(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFlagsReturned())));

        statsLeaderboards.add(new StatsLeaderboard("Mage Experience", CENTER_BOARD_1, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getMage().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getMage().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Warrior Experience", CENTER_BOARD_2, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getWarrior().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getWarrior().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Paladin Experience", CENTER_BOARD_3, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getPaladin().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getPaladin().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Shaman Experience", CENTER_BOARD_4, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getShaman().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getShaman().getExperience())));
        statsLeaderboards.add(new StatsLeaderboard("Rogue Experience", CENTER_BOARD_5, databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getRogue().getExperience(), databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getRogue().getExperience())));
    }

}
