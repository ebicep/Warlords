package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvEDifficultyStats;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager.SPAWN_POINT;

public class StatsLeaderboardPvE extends AbstractStatsLeaderboardGameType<DatabasePlayerPvEDifficultyStats> implements PvELeaderboard {

    private static final List<StatsLeaderboardCategory<DatabasePlayerPvEDifficultyStats>> CATEGORIES = new ArrayList<>() {{
        add(new StatsLeaderboardCategory<>(databasePlayer -> databasePlayer.getPveStats(), "All Modes", "All"));
    }};

    public StatsLeaderboardPvE() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "PvE";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabasePlayerPvEDifficultyStats> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();

        statsLeaderboards.add(new StatsLeaderboard("Masterworks Fair Wins", SPAWN_POINT,
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
        statsLeaderboards.add(new StatsLeaderboard("Average Masterworks Fair Placement", SPAWN_POINT,
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
            statsLeaderboards.add(new StatsLeaderboard("Masterworks Fair " + value.name + " Wins", SPAWN_POINT,
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
            statsLeaderboards.add(new StatsLeaderboard("Average Masterworks Fair " + value.name + " Placement", SPAWN_POINT,
                    databasePlayer -> {
                        List<MasterworksFairEntry> masterworksFairEntries = ((DatabasePlayerPvE) statsLeaderboardCategory.getStatFunction()
                                .apply(databasePlayer))
                                .getMasterworksFairEntries()
                                .stream()
                                .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                                .collect(Collectors.toList());
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
                                .collect(Collectors.toList());
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
