package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface MultiStat<DatabaseGameT extends DatabaseGameBase, DatabaseGamePlayerT extends DatabaseGamePlayerBase> extends Stats<DatabaseGameT, DatabaseGamePlayerT> {

    <T extends StatsWarlordsClasses<?, ?, ?, ?>> List<T> getStats();

    default <R> R getStat(Specializations spec, Function<Stats<?, ?>, R> statFunction, BinaryOperator<R> accumulator, R defaultValue) {
        return getStats()
                .stream()
                .map(s -> s.getSpec(spec))
                .map(statFunction)
                .reduce(accumulator)
                .orElse(defaultValue);
    }

    default <R> R getStat(Classes classes, Function<Stats<?, ?>, R> statFunction, BinaryOperator<R> accumulator, R defaultValue) {
        return getStats()
                .stream()
                .map(s -> s.getClass(classes))
                .flatMap(s -> Arrays.stream(s.getSpecs()))
                .map(statFunction)
                .reduce(accumulator)
                .orElse(defaultValue);
    }

    default <R> R getStat(Function<Stats<?, ?>, R> statFunction, BinaryOperator<R> accumulator, R defaultValue) {
        return getStats()
                .stream()
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
    default int getPlays() {
        return getStat(Stats::getPlays, Integer::sum, 0);
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


}
