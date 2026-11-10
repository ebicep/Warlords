package com.ebicep.warlords.database.repositories.player.pojos.cache;

import com.ebicep.warlords.database.repositories.player.pojos.general.MultiStatsGeneral;

/**
 * {@link MultiStatsGeneral} with aggregate getters served from the push-up cache.
 */
public interface CachedMultiStatsGeneral extends MultiStatsGeneral, CachedGeneralStats {

    @Override
    default int getKills() {
        return CachedGeneralStats.super.getKills();
    }

    @Override
    default int getAssists() {
        return CachedGeneralStats.super.getAssists();
    }

    @Override
    default int getDeaths() {
        return CachedGeneralStats.super.getDeaths();
    }

    @Override
    default int getWins() {
        return CachedGeneralStats.super.getWins();
    }

    @Override
    default int getLosses() {
        return CachedGeneralStats.super.getLosses();
    }

    @Override
    default int getPlays() {
        return CachedGeneralStats.super.getPlays();
    }

    @Override
    default long getDamage() {
        return CachedGeneralStats.super.getDamage();
    }

    @Override
    default long getHealing() {
        return CachedGeneralStats.super.getHealing();
    }

    @Override
    default long getAbsorbed() {
        return CachedGeneralStats.super.getAbsorbed();
    }

    @Override
    default long getExperience() {
        return CachedGeneralStats.super.getExperience();
    }

    @Override
    default void warmPushedStats() {
        pushedStats().warm(() -> pushedStats().fillGeneral(
                MultiStatsGeneral.super.getKills(),
                MultiStatsGeneral.super.getAssists(),
                MultiStatsGeneral.super.getDeaths(),
                MultiStatsGeneral.super.getWins(),
                MultiStatsGeneral.super.getLosses(),
                MultiStatsGeneral.super.getPlays(),
                MultiStatsGeneral.super.getDamage(),
                MultiStatsGeneral.super.getHealing(),
                MultiStatsGeneral.super.getAbsorbed(),
                MultiStatsGeneral.super.getExperience()
        ));
    }

    default int treeWalkKills() {
        return MultiStatsGeneral.super.getKills();
    }
}
