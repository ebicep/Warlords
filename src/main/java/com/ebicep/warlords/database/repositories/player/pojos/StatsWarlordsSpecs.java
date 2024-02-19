package com.ebicep.warlords.database.repositories.player.pojos;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface StatsWarlordsSpecs<T extends Stats> extends Stats, DatabaseWarlordsSpecs<T> {
    @Nonnull
    private <NumT> NumT getStat(Function<Stats, NumT> statFunction, BinaryOperator<NumT> accumulator, NumT defaultValue) {
        return Arrays.stream(getSpecs())
                     .map(statFunction)
                     .reduce(accumulator)
                     .orElse(defaultValue);
    }

    @Override
    default int getDeaths() {
        return getStat(Stats::getDeaths, Integer::sum, 0);
    }

    @Override
    default int getKills() {
        return getStat(Stats::getKills, Integer::sum, 0);
    }

    @Override
    default int getAssists() {
        return getStat(Stats::getAssists, Integer::sum, 0);
    }

    @Override
    default int getLosses() {
        return getStat(Stats::getLosses, Integer::sum, 0);
    }

    @Override
    default int getWins() {
        return getStat(Stats::getWins, Integer::sum, 0);
    }

    @Override
    default long getDamage() {
        return getStat(Stats::getDamage, Long::sum, 0L);
    }

    @Override
    default long getHealing() {
        return getStat(Stats::getHealing, Long::sum, 0L);
    }

    @Override
    default long getAbsorbed() {
        return getStat(Stats::getAbsorbed, Long::sum, 0L);
    }

    @Override
    default long getExperience() {
        return getStat(Stats::getExperience, Long::sum, 0L);
    }

    @Override
    default int getPlays() {
        return getStat(Stats::getPlays, Integer::sum, 0);
    }

    @Override
    default void setExperience(long experience) {
        // only set for specs
    }
}
