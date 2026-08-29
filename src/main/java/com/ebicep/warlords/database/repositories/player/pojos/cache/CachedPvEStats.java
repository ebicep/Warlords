package com.ebicep.warlords.database.repositories.player.pojos.cache;

import java.util.Map;

/**
 * PvE getters backed by {@link PushedStatsOwner}, including mob maps and time played.
 */
public interface CachedPvEStats extends CachedGeneralStats {

    default long getTotalTimePlayed() {
        return pushedTotalTimePlayed();
    }

    default Map<String, Long> getMobKills() {
        return pushedMobKills();
    }

    default Map<String, Long> getMobAssists() {
        return pushedMobAssists();
    }

    default Map<String, Long> getMobDeaths() {
        return pushedMobDeaths();
    }
}
