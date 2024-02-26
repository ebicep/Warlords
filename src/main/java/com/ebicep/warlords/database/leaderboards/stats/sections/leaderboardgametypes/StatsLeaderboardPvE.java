package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractMultiStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.MultiStatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.*;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager.MAIN_LOBBY_SPAWN;

class MultiStatsLeaderboardCategoryPvE extends MultiStatsLeaderboardCategory<PvEStatsWarlordsClasses<
        DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
        DatabaseGamePlayerPvEBase,
        PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
        PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>,
        DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
        DatabaseGamePlayerPvEBase,
        PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
        PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                DatabaseGamePlayerPvEBase,
                PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>,
        MultiPvEStats<PvEStatsWarlordsClasses<
                DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                DatabaseGamePlayerPvEBase,
                PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
                PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>,
                DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                DatabaseGamePlayerPvEBase,
                PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
                PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                        DatabaseGamePlayerPvEBase,
                        PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>> {

    public MultiStatsLeaderboardCategoryPvE(
            Function<DatabasePlayer, MultiPvEStats<PvEStatsWarlordsClasses<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>, PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>, DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>, PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>> databasePlayerMultiPvEStatsFunction,
            String categoryName,
            String shortName
    ) {
        super(databasePlayerMultiPvEStatsFunction, categoryName, shortName);
    }
}


public class StatsLeaderboardPvE extends AbstractMultiStatsLeaderboardGameType<
        PvEStatsWarlordsClasses<
                DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                DatabaseGamePlayerPvEBase,
                PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
                PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>,
        DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
        DatabaseGamePlayerPvEBase,
        PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
        PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                DatabaseGamePlayerPvEBase,
                PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>,
        MultiPvEStats<PvEStatsWarlordsClasses<
                DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                DatabaseGamePlayerPvEBase,
                PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
                PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>,
                DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                DatabaseGamePlayerPvEBase,
                PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>,
                PvEStatsWarlordsSpecs<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
                        DatabaseGamePlayerPvEBase,
                        PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>>,
        MultiStatsLeaderboardCategoryPvE>
        implements PvELeaderboard {

    private static final List<MultiStatsLeaderboardCategoryPvE> CATEGORIES = new ArrayList<>() {{
        add(new MultiStatsLeaderboardCategoryPvE(DatabasePlayer::getPveStats, "All Modes", "All"));
    }};

    public StatsLeaderboardPvE() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "PvE";
    }

    @Override
    public void addExtraLeaderboards(MultiStatsLeaderboardCategoryPvE statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();

        statsLeaderboards.add(new StatsLeaderboard("Masterworks Fair Wins", MAIN_LOBBY_SPAWN,
                databasePlayer -> ((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction().apply(databasePlayer)).getMasterworksFairEntries().stream()
                                                                                                                        .filter(masterworksFairEntry -> masterworksFairEntry.getPlacement() == 1)
                                                                                                                        .count(),
                databasePlayer -> NumberFormat.addCommaAndRound(((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction().apply(databasePlayer))
                        .getMasterworksFairEntries()
                        .stream()
                        .filter(masterworksFairEntry -> masterworksFairEntry.getPlacement() == 1)
                        .count()),
                databasePlayer -> !(statsLeaderboardCategory.getStatFunction().apply(databasePlayer) instanceof DatabasePlayerPvE),
                true
        ));
        statsLeaderboards.add(new StatsLeaderboard("Average Masterworks Fair Placement", MAIN_LOBBY_SPAWN,
                databasePlayer -> {
                    List<MasterworksFairEntry> masterworksFairEntries = ((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction().apply(databasePlayer))
                            .getMasterworksFairEntries();
                    if (masterworksFairEntries.isEmpty()) {
                        return 0;
                    }
                    return (double) masterworksFairEntries.stream()
                                                          .mapToInt(MasterworksFairEntry::getPlacement)
                                                          .sum() / masterworksFairEntries.size();
                },
                databasePlayer -> {
                    List<MasterworksFairEntry> masterworksFairEntries = ((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction().apply(databasePlayer))
                            .getMasterworksFairEntries();
                    if (masterworksFairEntries.isEmpty()) {
                        return "0";
                    }
                    return NumberFormat.formatOptionalHundredths(
                            (double) masterworksFairEntries.stream()
                                                           .mapToInt(MasterworksFairEntry::getPlacement)
                                                           .sum() / masterworksFairEntries.size());
                },
                databasePlayer -> !(statsLeaderboardCategory.getStatFunction().apply(databasePlayer) instanceof DatabasePlayerPvE),
                true
        ));
        for (WeaponsPvE value : WeaponsPvE.VALUES) {
            if (value.getPlayerEntries == null) {
                continue;
            }
            statsLeaderboards.add(new StatsLeaderboard("Masterworks Fair " + value.name + " Wins", MAIN_LOBBY_SPAWN,
                    databasePlayer -> ((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction().apply(databasePlayer)).getMasterworksFairEntries()
                                                                                                                            .stream()
                                                                                                                            .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value && masterworksFairEntry.getPlacement() == 1)
                                                                                                                            .count(),
                    databasePlayer -> NumberFormat.addCommaAndRound(((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction().apply(databasePlayer))
                            .getMasterworksFairEntries()
                            .stream()
                            .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value && masterworksFairEntry.getPlacement() == 1)
                            .count()),
                    databasePlayer -> !(statsLeaderboardCategory.getStatFunction().apply(databasePlayer) instanceof DatabasePlayerPvE),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard("Average Masterworks Fair " + value.name + " Placement", MAIN_LOBBY_SPAWN,
                    databasePlayer -> {
                        List<MasterworksFairEntry> masterworksFairEntries = ((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction()
                                                                                                                         .apply(databasePlayer))
                                .getMasterworksFairEntries()
                                .stream()
                                .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                                .toList();
                        if (masterworksFairEntries.isEmpty()) {
                            return 0;
                        }
                        return (double) masterworksFairEntries.stream()
                                                              .mapToInt(MasterworksFairEntry::getPlacement)
                                                              .sum() / masterworksFairEntries.size();
                    },
                    databasePlayer -> {
                        List<MasterworksFairEntry> masterworksFairEntries = ((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction()
                                                                                                                         .apply(databasePlayer))
                                .getMasterworksFairEntries()
                                .stream()
                                .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                                .toList();
                        if (masterworksFairEntries.isEmpty()) {
                            return "0";
                        }
                        return NumberFormat.formatOptionalHundredths((double) masterworksFairEntries.stream()
                                                                                                    .mapToInt(MasterworksFairEntry::getPlacement)
                                                                                                    .sum() / masterworksFairEntries.size());
                    },
                    databasePlayer -> !(statsLeaderboardCategory.getStatFunction().apply(databasePlayer) instanceof DatabasePlayerPvE),
                    true
            ));
        }
    }


}
