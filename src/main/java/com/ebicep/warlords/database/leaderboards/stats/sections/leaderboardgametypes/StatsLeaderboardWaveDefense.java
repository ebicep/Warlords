package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabasePlayerPvEWaveDefenseDifficultyStats;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

public class StatsLeaderboardWaveDefense extends AbstractStatsLeaderboardGameType<DatabasePlayerPvEWaveDefenseDifficultyStats> implements PvELeaderboard {

    private static final List<StatsLeaderboardCategory<DatabasePlayerPvEWaveDefenseDifficultyStats>> CATEGORIES = new ArrayList<>() {{
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats(), "All Modes", "All"));
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getEasyStats(), "Easy Mode", "Easy"));
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getNormalStats(), "Normal Mode", "Normal"));
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getHardStats(), "Hard Mode", "Hard"));
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getEndlessStats(), "Endless Mode", "Endless"));
    }};

    public StatsLeaderboardWaveDefense() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "Wave Defense";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabasePlayerPvEWaveDefenseDifficultyStats> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();

        statsLeaderboards.add(new StatsLeaderboard("Waves Cleared",
                CIRCULAR_1_OUTER_2,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getTotalWavesCleared(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getTotalWavesCleared())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Clear Rate",
                LEAD_5,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClearRate(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClearRate() * 100) + "%"
        ));
        statsLeaderboards.add(new StatsLeaderboard("Fastest Win", UPPER_CENTER_1,
                databasePlayer -> -statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFastestGameFinished(),
                databasePlayer -> Utils.formatTimeLeft(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFastestGameFinished() / 20),
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFastestGameFinished() == 0
        ));
        statsLeaderboards.add(new StatsLeaderboard("Highest Wave Cleared", UPPER_CENTER_2,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getHighestWaveCleared(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                                                                                        .apply(databasePlayer)
                                                                                        .getHighestWaveCleared())
        ));
    }

}
