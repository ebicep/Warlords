package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractMultiStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.MultiStatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.MultiPvEWaveDefenseStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.WaveDefenseStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.WaveDefenseStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.WaveDefenseStatsWarlordsSpecs;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;

class MultiStatsLeaderboardCategoryWaveDefense extends MultiStatsLeaderboardCategory<WaveDefenseStatsWarlordsClasses,
        DatabaseGamePvEWaveDefense,
        DatabaseGamePlayerPvEWaveDefense,
        WaveDefenseStats,
        WaveDefenseStatsWarlordsSpecs,
        MultiPvEWaveDefenseStats> {

    public MultiStatsLeaderboardCategoryWaveDefense(
            Function<DatabasePlayer, MultiPvEWaveDefenseStats> databasePlayerMultiPvEWaveDefenseFunction,
            String categoryName,
            String shortName
    ) {
        super(databasePlayerMultiPvEWaveDefenseFunction, categoryName, shortName);
    }
}

public class StatsLeaderboardWaveDefense extends AbstractMultiStatsLeaderboardGameType<
        WaveDefenseStatsWarlordsClasses,
        DatabaseGamePvEWaveDefense,
        DatabaseGamePlayerPvEWaveDefense,
        WaveDefenseStats,
        WaveDefenseStatsWarlordsSpecs,
        MultiPvEWaveDefenseStats,
        MultiStatsLeaderboardCategoryWaveDefense> implements PvELeaderboard {

    private static final List<MultiStatsLeaderboardCategoryWaveDefense> CATEGORIES = new ArrayList<>() {{
        add(new MultiStatsLeaderboardCategoryWaveDefense(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats(), "All Modes", "All"));
        add(new MultiStatsLeaderboardCategoryWaveDefense(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getEasyStats(), "Easy Mode", "Easy"));
        add(new MultiStatsLeaderboardCategoryWaveDefense(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getNormalStats(), "Normal Mode", "Normal"));
        add(new MultiStatsLeaderboardCategoryWaveDefense(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getHardStats(), "Hard Mode", "Hard"));
        add(new MultiStatsLeaderboardCategoryWaveDefense(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getExtremeStats(), "Extreme Mode", "Extreme"));
        add(new MultiStatsLeaderboardCategoryWaveDefense(databasePlayer -> databasePlayer.getPveStats().getWaveDefenseStats().getEndlessStats(), "Endless Mode", "Endless"));
    }};

    public StatsLeaderboardWaveDefense() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "Wave Defense";
    }

    @Override
    public void addExtraLeaderboards(MultiStatsLeaderboardCategoryWaveDefense statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();

        statsLeaderboards.add(new StatsLeaderboard("Waves Cleared",
                CIRCULAR_1_OUTER_2,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getTotalWavesCleared(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getTotalWavesCleared())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Clear Rate",
                LEAD_5,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getWinRate(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getWinRate() * 100) + "%"
        ));
        statsLeaderboards.add(new StatsLeaderboard("Fastest Win", UPPER_CENTER_1,
                databasePlayer -> -statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFastestGameFinished(),
                databasePlayer -> StringUtils.formatTimeLeft(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getFastestGameFinished() / 20),
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
