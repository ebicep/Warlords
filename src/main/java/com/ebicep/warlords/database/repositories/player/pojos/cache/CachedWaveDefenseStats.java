package com.ebicep.warlords.database.repositories.player.pojos.cache;

/**
 * Wave-defense getters backed by {@link PushedStatsOwner}.
 */
public interface CachedWaveDefenseStats extends CachedPvEStats {

    default int getTotalWavesCleared() {
        return pushedTotalWavesCleared();
    }

    default int getHighestWaveCleared() {
        return pushedHighestWaveCleared();
    }

    default long getFastestGameFinished() {
        return pushedFastestGameFinished();
    }

    default long getMostDamageInWave() {
        return pushedMostDamageInWave();
    }
}
