package com.ebicep.warlords.database.repositories.player.pojos.cache;

/**
 * Default Stats getters backed by {@link PushedStatsOwner}.
 * Bridge interfaces that also extend a Multi* or WarlordsClasses type must re-declare these
 * to resolve default-method conflicts in favor of the pushed cache.
 */
public interface CachedGeneralStats extends PushedStatsOwner {

    default int getKills() {
        return pushedKills();
    }

    default int getAssists() {
        return pushedAssists();
    }

    default int getDeaths() {
        return pushedDeaths();
    }

    default int getWins() {
        return pushedWins();
    }

    default int getLosses() {
        return pushedLosses();
    }

    default int getPlays() {
        return pushedPlays();
    }

    default long getDamage() {
        return pushedDamage();
    }

    default long getHealing() {
        return pushedHealing();
    }

    default long getAbsorbed() {
        return pushedAbsorbed();
    }

    default long getExperience() {
        return pushedExperience();
    }
}
