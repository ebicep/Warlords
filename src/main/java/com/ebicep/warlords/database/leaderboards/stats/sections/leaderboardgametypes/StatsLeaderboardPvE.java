package com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes;

import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairEntry;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;

import java.util.List;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations.*;
import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager.SPAWN_POINT;

public class StatsLeaderboardPvE extends AbstractStatsLeaderboardGameType<DatabasePlayerPvE> {

    public StatsLeaderboardPvE() {
        super(
                new StatsLeaderboardCategory<>(DatabasePlayer::getPveStats, "All Queues"),
                new StatsLeaderboardCategory<>(DatabasePlayer::getPveStats, "All Queues"),
                new StatsLeaderboardCategory<>(DatabasePlayer::getPveStats, "All Queues")
        );
    }

    @Override
    public String getSubTitle() {
        return "PvE";
    }

    @Override
    public void addExtraLeaderboards(StatsLeaderboardCategory<DatabasePlayerPvE> statsLeaderboardCategory) {
        List<StatsLeaderboard> statsLeaderboards = statsLeaderboardCategory.getLeaderboards();
        statsLeaderboards.add(new StatsLeaderboard("Mage Experience",
                CENTER_BOARD_1,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getMage().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getMage().getExperience())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Warrior Experience",
                CENTER_BOARD_2,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getWarrior().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getWarrior().getExperience())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Paladin Experience",
                CENTER_BOARD_3,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getPaladin().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getPaladin().getExperience())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Shaman Experience",
                CENTER_BOARD_4,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getShaman().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getShaman().getExperience())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Rogue Experience",
                CENTER_BOARD_5,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getRogue().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getRogue().getExperience())
        ));

        statsLeaderboards.add(new StatsLeaderboard("Waves Cleared",
                CIRCULAR_1_OUTER_2,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getTotalWavesCleared(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getTotalWavesCleared())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Clear Rate",
                LEAD_5,
                databasePlayer -> {
                    DatabasePlayerPvE databasePlayerPvE = statsLeaderboardCategory.getStatFunction().apply(databasePlayer);
                    int plays = databasePlayerPvE.getNormalStats().getPlays() + databasePlayerPvE.getHardStats().getPlays();
                    return plays == 0 ? 0 : (databasePlayerPvE.getNormalStats().getWins() + databasePlayerPvE.getHardStats().getWins()) / plays;
                },
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getWinRate() * 100) + "%"
        ));
        statsLeaderboards.add(new StatsLeaderboard("Fastest Normal Win", UPPER_CENTER_1,
                databasePlayer -> -statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getNormalStats().getFastestGameFinished(),
                databasePlayer -> Utils.formatTimeLeft(statsLeaderboardCategory.getStatFunction()
                        .apply(databasePlayer)
                        .getNormalStats()
                        .getFastestGameFinished() / 20),
                databasePlayer -> databasePlayer.getPveStats().getNormalStats().getFastestGameFinished() == 0
        ));
        statsLeaderboards.add(new StatsLeaderboard("Highest Endless Wave Cleared", UPPER_CENTER_2,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getEndlessStats().getHighestWaveCleared(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                        .apply(databasePlayer)
                        .getEndlessStats()
                        .getHighestWaveCleared())
        ));
        statsLeaderboards.add(new StatsLeaderboard("Fastest Hard Win", UPPER_CENTER_3,
                databasePlayer -> -statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getHardStats().getFastestGameFinished(),
                databasePlayer -> Utils.formatTimeLeft(statsLeaderboardCategory.getStatFunction()
                        .apply(databasePlayer)
                        .getHardStats()
                        .getFastestGameFinished() / 20),
                databasePlayer -> databasePlayer.getPveStats().getHardStats().getFastestGameFinished() == 0
        ));

        statsLeaderboards.add(new StatsLeaderboard("Masterworks Fair Wins", SPAWN_POINT,
                databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getMasterworksFairEntries().stream()
                        .filter(masterworksFairEntry -> masterworksFairEntry.getPlacement() == 1)
                        .count(),
                databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                        .apply(databasePlayer)
                        .getMasterworksFairEntries()
                        .stream()
                        .filter(masterworksFairEntry -> masterworksFairEntry.getPlacement() == 1)
                        .count()),
                true
        ));
        statsLeaderboards.add(new StatsLeaderboard("Average Masterworks Fair Placement", SPAWN_POINT,
                databasePlayer -> {
                    List<MasterworksFairEntry> masterworksFairEntries = statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getMasterworksFairEntries();
                    if (masterworksFairEntries.isEmpty()) {
                        return 0;
                    }
                    return (double) masterworksFairEntries.stream()
                            .mapToInt(MasterworksFairEntry::getPlacement)
                            .sum() / masterworksFairEntries.size();
                },
                databasePlayer -> {
                    List<MasterworksFairEntry> masterworksFairEntries = statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getMasterworksFairEntries();
                    if (masterworksFairEntries.isEmpty()) {
                        return "0";
                    }
                    return NumberFormat.formatOptionalHundredths(
                            (double) masterworksFairEntries.stream()
                                    .mapToInt(MasterworksFairEntry::getPlacement)
                                    .sum() / masterworksFairEntries.size());
                },
                true
        ));
        for (WeaponsPvE value : WeaponsPvE.VALUES) {
            if (value.getPlayerEntries == null) {
                continue;
            }
            statsLeaderboards.add(new StatsLeaderboard("Masterworks Fair " + value.name + " Wins", SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getMasterworksFairEntries().stream()
                            .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value && masterworksFairEntry.getPlacement() == 1)
                            .count(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getMasterworksFairEntries()
                            .stream()
                            .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value && masterworksFairEntry.getPlacement() == 1)
                            .count()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard("Average Masterworks Fair " + value.name + " Placement", SPAWN_POINT,
                    databasePlayer -> {
                        List<MasterworksFairEntry> masterworksFairEntries = statsLeaderboardCategory.getStatFunction()
                                .apply(databasePlayer)
                                .getMasterworksFairEntries();
                        if (masterworksFairEntries.isEmpty()) {
                            return 0;
                        }
                        return (double) masterworksFairEntries.stream()
                                .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                                .mapToInt(MasterworksFairEntry::getPlacement)
                                .sum() / masterworksFairEntries.size();
                    },
                    databasePlayer -> {
                        List<MasterworksFairEntry> masterworksFairEntries = statsLeaderboardCategory.getStatFunction()
                                .apply(databasePlayer)
                                .getMasterworksFairEntries();
                        if (masterworksFairEntries.isEmpty()) {
                            return "0";
                        }
                        return NumberFormat.formatOptionalHundredths(
                                (double) masterworksFairEntries.stream()
                                        .filter(masterworksFairEntry -> masterworksFairEntry.getRarity() == value)
                                        .mapToInt(MasterworksFairEntry::getPlacement)
                                        .sum() / masterworksFairEntries.size());
                    },
                    true
            ));
        }

        for (Classes value : Classes.VALUES) {
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Level",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getLevel(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getClass(value)
                            .getLevel()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Kills",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getKills(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getClass(value)
                            .getKills()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Assists",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getAssists(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getClass(value)
                            .getAssists()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Deaths",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getDeaths(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getClass(value)
                            .getDeaths()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Waves Cleared",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getTotalWavesCleared(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getClass(value)
                            .getTotalWavesCleared()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Wins",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getWins(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getWins()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Clear Rate",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getClass(value).getWinRate(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getClass(value)
                            .getWinRate() * 100) + "%",
                    true
            ));
        }

        for (Specializations value : Specializations.VALUES) {
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Level",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getLevel(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getLevel()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Kills",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getKills(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getKills()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Assists",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getAssists(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getSpec(value)
                            .getAssists()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Deaths",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getDeaths(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getSpec(value)
                            .getDeaths()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Waves Cleared",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getTotalWavesCleared(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getSpec(value)
                            .getTotalWavesCleared()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Wins",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getWins(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getWins()),
                    true
            ));
            statsLeaderboards.add(new StatsLeaderboard(value.name + " Clear Rate",
                    SPAWN_POINT,
                    databasePlayer -> statsLeaderboardCategory.getStatFunction().apply(databasePlayer).getSpec(value).getWinRate(),
                    databasePlayer -> NumberFormat.addCommaAndRound(statsLeaderboardCategory.getStatFunction()
                            .apply(databasePlayer)
                            .getSpec(value)
                            .getWinRate() * 100) + "%",
                    true
            ));
        }
    }

}
