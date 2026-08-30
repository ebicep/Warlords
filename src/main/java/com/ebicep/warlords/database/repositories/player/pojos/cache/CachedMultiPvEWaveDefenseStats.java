package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.MultiPvEWaveDefenseStats;

import java.util.Map;

/**
 * {@link MultiPvEWaveDefenseStats} with aggregate getters served from the push-up cache.
 */
public interface CachedMultiPvEWaveDefenseStats extends MultiPvEWaveDefenseStats, PushedMultiPvEStats, CachedWaveDefenseStats {

    @Override
    default int getKills() {
        return PushedMultiPvEStats.super.getKills();
    }

    @Override
    default int getAssists() {
        return PushedMultiPvEStats.super.getAssists();
    }

    @Override
    default int getDeaths() {
        return PushedMultiPvEStats.super.getDeaths();
    }

    @Override
    default int getWins() {
        return PushedMultiPvEStats.super.getWins();
    }

    @Override
    default int getLosses() {
        return PushedMultiPvEStats.super.getLosses();
    }

    @Override
    default int getPlays() {
        return PushedMultiPvEStats.super.getPlays();
    }

    @Override
    default long getDamage() {
        return PushedMultiPvEStats.super.getDamage();
    }

    @Override
    default long getHealing() {
        return PushedMultiPvEStats.super.getHealing();
    }

    @Override
    default long getAbsorbed() {
        return PushedMultiPvEStats.super.getAbsorbed();
    }

    @Override
    default long getExperience() {
        return PushedMultiPvEStats.super.getExperience();
    }

    @Override
    default long getTotalTimePlayed() {
        return PushedMultiPvEStats.super.getTotalTimePlayed();
    }

    @Override
    default Map<String, Long> getMobKills() {
        return PushedMultiPvEStats.super.getMobKills();
    }

    @Override
    default Map<String, Long> getMobAssists() {
        return PushedMultiPvEStats.super.getMobAssists();
    }

    @Override
    default Map<String, Long> getMobDeaths() {
        return PushedMultiPvEStats.super.getMobDeaths();
    }

    @Override
    default long getTotalMobKills() {
        return PushedMultiPvEStats.super.getTotalMobKills();
    }

    @Override
    default int getTotalWavesCleared() {
        return CachedWaveDefenseStats.super.getTotalWavesCleared();
    }

    @Override
    default int getHighestWaveCleared() {
        return CachedWaveDefenseStats.super.getHighestWaveCleared();
    }

    @Override
    default long getFastestGameFinished() {
        return CachedWaveDefenseStats.super.getFastestGameFinished();
    }

    @Override
    default long getMostDamageInWave() {
        return CachedWaveDefenseStats.super.getMostDamageInWave();
    }

    @Override
    default void warmPushedStats() {
        pushedStats().warm(() -> {
            pushedStats().fillGeneral(
                    MultiPvEWaveDefenseStats.super.getKills(),
                    MultiPvEWaveDefenseStats.super.getAssists(),
                    MultiPvEWaveDefenseStats.super.getDeaths(),
                    MultiPvEWaveDefenseStats.super.getWins(),
                    MultiPvEWaveDefenseStats.super.getLosses(),
                    MultiPvEWaveDefenseStats.super.getPlays(),
                    MultiPvEWaveDefenseStats.super.getDamage(),
                    MultiPvEWaveDefenseStats.super.getHealing(),
                    MultiPvEWaveDefenseStats.super.getAbsorbed(),
                    MultiPvEWaveDefenseStats.super.getExperience()
            );
            pushedStats().fillPvE(
                    MultiPvEWaveDefenseStats.super.getTotalTimePlayed(),
                    MultiPvEWaveDefenseStats.super.getMobKills(),
                    MultiPvEWaveDefenseStats.super.getMobAssists(),
                    MultiPvEWaveDefenseStats.super.getMobDeaths()
            );
            pushedStats().fillTotalWavesCleared(MultiPvEWaveDefenseStats.super.getTotalWavesCleared());
            pushedStats().fillWaveDefense(
                    MultiPvEWaveDefenseStats.super.getHighestWaveCleared(),
                    MultiPvEWaveDefenseStats.super.getFastestGameFinished(),
                    MultiPvEWaveDefenseStats.super.getMostDamageInWave()
            );
        });
    }

    default int treeWalkKills() {
        return MultiPvEWaveDefenseStats.super.getKills();
    }

    default Map<String, Long> treeWalkMobKills() {
        return MultiPvEWaveDefenseStats.super.getMobKills();
    }
}
