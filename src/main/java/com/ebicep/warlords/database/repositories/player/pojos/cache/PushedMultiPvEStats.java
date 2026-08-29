package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.player.pojos.pve.MultiPvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.MultiPvEAnomalyStats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.MultiPvEOnslaughtStats;

import java.util.Map;

/**
 * Resolves {@link MultiPvEStats} vs {@link CachedPvEStats} defaults in favor of the push-up cache.
 * Does not extend raw {@code MultiPvEStats} (avoids typed/raw clashes). Mode aggregators use the
 * nested bridges, or implement {@code MultiPvEStats<...>} plus this type with thin class overrides.
 */
public interface PushedMultiPvEStats extends CachedPvEStats {

    @Override
    default int getKills() {
        return CachedPvEStats.super.getKills();
    }

    @Override
    default int getAssists() {
        return CachedPvEStats.super.getAssists();
    }

    @Override
    default int getDeaths() {
        return CachedPvEStats.super.getDeaths();
    }

    @Override
    default int getWins() {
        return CachedPvEStats.super.getWins();
    }

    @Override
    default int getLosses() {
        return CachedPvEStats.super.getLosses();
    }

    @Override
    default int getPlays() {
        return CachedPvEStats.super.getPlays();
    }

    @Override
    default long getDamage() {
        return CachedPvEStats.super.getDamage();
    }

    @Override
    default long getHealing() {
        return CachedPvEStats.super.getHealing();
    }

    @Override
    default long getAbsorbed() {
        return CachedPvEStats.super.getAbsorbed();
    }

    @Override
    default long getExperience() {
        return CachedPvEStats.super.getExperience();
    }

    @Override
    default long getTotalTimePlayed() {
        return CachedPvEStats.super.getTotalTimePlayed();
    }

    @Override
    default Map<String, Long> getMobKills() {
        return CachedPvEStats.super.getMobKills();
    }

    @Override
    default Map<String, Long> getMobAssists() {
        return CachedPvEStats.super.getMobAssists();
    }

    @Override
    default Map<String, Long> getMobDeaths() {
        return CachedPvEStats.super.getMobDeaths();
    }

    interface Onslaught extends MultiPvEOnslaughtStats, PushedMultiPvEStats {
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
        default void warmPushedStats() {
            pushedStats().warm(() -> {
                pushedStats().fillGeneral(
                        MultiPvEOnslaughtStats.super.getKills(),
                        MultiPvEOnslaughtStats.super.getAssists(),
                        MultiPvEOnslaughtStats.super.getDeaths(),
                        MultiPvEOnslaughtStats.super.getWins(),
                        MultiPvEOnslaughtStats.super.getLosses(),
                        MultiPvEOnslaughtStats.super.getPlays(),
                        MultiPvEOnslaughtStats.super.getDamage(),
                        MultiPvEOnslaughtStats.super.getHealing(),
                        MultiPvEOnslaughtStats.super.getAbsorbed(),
                        MultiPvEOnslaughtStats.super.getExperience()
                );
                pushedStats().fillPvE(
                        MultiPvEOnslaughtStats.super.getTotalTimePlayed(),
                        MultiPvEOnslaughtStats.super.getMobKills(),
                        MultiPvEOnslaughtStats.super.getMobAssists(),
                        MultiPvEOnslaughtStats.super.getMobDeaths()
                );
            });
        }
    }

    interface Anomaly extends MultiPvEAnomalyStats, PushedMultiPvEStats {
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
        default void warmPushedStats() {
            pushedStats().warm(() -> {
                pushedStats().fillGeneral(
                        MultiPvEAnomalyStats.super.getKills(),
                        MultiPvEAnomalyStats.super.getAssists(),
                        MultiPvEAnomalyStats.super.getDeaths(),
                        MultiPvEAnomalyStats.super.getWins(),
                        MultiPvEAnomalyStats.super.getLosses(),
                        MultiPvEAnomalyStats.super.getPlays(),
                        MultiPvEAnomalyStats.super.getDamage(),
                        MultiPvEAnomalyStats.super.getHealing(),
                        MultiPvEAnomalyStats.super.getAbsorbed(),
                        MultiPvEAnomalyStats.super.getExperience()
                );
                pushedStats().fillPvE(
                        MultiPvEAnomalyStats.super.getTotalTimePlayed(),
                        MultiPvEAnomalyStats.super.getMobKills(),
                        MultiPvEAnomalyStats.super.getMobAssists(),
                        MultiPvEAnomalyStats.super.getMobDeaths()
                );
            });
        }
    }
}
