package com.ebicep.warlords.database.repositories.player.pojos.cache;

/**
 * Wave-defense getters backed by {@link PushedStatsOwner}.
 */
public interface CachedWaveDefenseStats extends CachedPvEStats {

    default int getTotalWavesCleared() {
        return pushedTotalWavesCleared();
    }
}
