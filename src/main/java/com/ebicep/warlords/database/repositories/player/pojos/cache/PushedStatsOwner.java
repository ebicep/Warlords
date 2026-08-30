package com.ebicep.warlords.database.repositories.player.pojos.cache;

import java.util.Map;

public interface PushedStatsOwner {

    PushedStatTotals pushedStats();

    void warmPushedStats();

    default void invalidatePushedStats() {
        pushedStats().invalidate();
    }

    default void rebuildPushedStats() {
        invalidatePushedStats();
        warmPushedStats();
    }

    default int pushedKills() {
        warmPushedStats();
        return pushedStats().getKills();
    }

    default int pushedAssists() {
        warmPushedStats();
        return pushedStats().getAssists();
    }

    default int pushedDeaths() {
        warmPushedStats();
        return pushedStats().getDeaths();
    }

    default int pushedWins() {
        warmPushedStats();
        return pushedStats().getWins();
    }

    default int pushedLosses() {
        warmPushedStats();
        return pushedStats().getLosses();
    }

    default int pushedPlays() {
        warmPushedStats();
        return pushedStats().getPlays();
    }

    default long pushedDamage() {
        warmPushedStats();
        return pushedStats().getDamage();
    }

    default long pushedHealing() {
        warmPushedStats();
        return pushedStats().getHealing();
    }

    default long pushedAbsorbed() {
        warmPushedStats();
        return pushedStats().getAbsorbed();
    }

    default long pushedExperience() {
        warmPushedStats();
        return pushedStats().getExperience();
    }

    default long pushedTotalTimePlayed() {
        warmPushedStats();
        return pushedStats().getTotalTimePlayed();
    }

    default int pushedTotalWavesCleared() {
        warmPushedStats();
        return pushedStats().getTotalWavesCleared();
    }

    default Map<String, Long> pushedMobKills() {
        warmPushedStats();
        return pushedStats().getMobKillsView();
    }

    default Map<String, Long> pushedMobAssists() {
        warmPushedStats();
        return pushedStats().getMobAssistsView();
    }

    default Map<String, Long> pushedMobDeaths() {
        warmPushedStats();
        return pushedStats().getMobDeathsView();
    }

    default long pushedTotalMobKills() {
        warmPushedStats();
        return pushedStats().getTotalMobKills();
    }

    default long pushedMobKillCount(String mobName) {
        warmPushedStats();
        return pushedStats().getMobKillCount(mobName);
    }

    default int pushedHighestWaveCleared() {
        warmPushedStats();
        return pushedStats().getHighestWaveCleared();
    }

    default long pushedFastestGameFinished() {
        warmPushedStats();
        return pushedStats().getFastestGameFinished();
    }

    default long pushedMostDamageInWave() {
        warmPushedStats();
        return pushedStats().getMostDamageInWave();
    }

    default long pushedLongestTicksLived() {
        warmPushedStats();
        return pushedStats().getLongestTicksLived();
    }
}
