package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStats;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.java.NumberFormat;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager.MAIN_LOBBY_SPAWN;

public class StatsLeaderboardPvE extends AbstractStatsLeaderboardGameType<
        DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>,
        DatabaseGamePlayerPvEBase,
        PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>
        implements PvELeaderboard {

    private static final List<StatsLeaderboardCategory<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>>> CATEGORIES = new ArrayList<>() {{
        add(new StatsLeaderboardCategory<>(DatabasePlayer::getPveStats, "All Modes", "All"));
    }};

    public StatsLeaderboardPvE() {
        super(CATEGORIES);
    }

    @Override
    public String getSubTitle() {
        return "PvE";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase, PvEStats<DatabaseGamePvEBase<DatabaseGamePlayerPvEBase>, DatabaseGamePlayerPvEBase>> statsLeaderboardCategory) {
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
