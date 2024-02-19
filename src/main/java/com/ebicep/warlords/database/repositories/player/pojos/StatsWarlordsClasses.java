package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface StatsWarlordsClasses<T extends Stats, R extends DatabaseWarlordsSpecs<T>> extends Stats, DatabaseWarlordsClasses<T, R> {

    @Override
    default T getSpec(Specializations specializations) {
        return getClass(Specializations.getClass(specializations)).getSpecs()[specializations.specType.ordinal()];
    }

    @Override
    default R getClass(Classes classes) {
        return getClasses()[classes.ordinal()];
    }

    @Override
    default R[] getClasses() {
        return (R[]) new DatabaseWarlordsSpecs[]{getMage(), getWarrior(), getPaladin(), getShaman(), getRogue(), getArcanist()};
    }

    @Override
    default int getDeaths() {
        return getStat(Stats::getDeaths, Integer::sum, 0);
    }

    @Nonnull
    private <NumT> NumT getStat(Function<Stats, NumT> statFunction, BinaryOperator<NumT> accumulator, NumT defaultValue) {
        return Arrays.stream(Specializations.VALUES)
                     .map(spec -> statFunction.apply(getSpec(spec)))
                     .reduce(accumulator)
                     .orElse(defaultValue);
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

    @Override
    default void setExperience(long experience) {
        // only set for specs
    }

    @Override
    default R getMage() {
        return getClass(Classes.MAGE);
    }

    @Override
    default R getPaladin() {
        return getClass(Classes.PALADIN);
    }

    @Override
    default R getShaman() {
        return getClass(Classes.SHAMAN);
    }

    @Override
    default R getWarrior() {
        return getClass(Classes.WARRIOR);
    }

    @Override
    default R getRogue() {
        return getClass(Classes.ROGUE);
    }

    @Override
    default R getArcanist() {
        return getClass(Classes.ARCANIST);
    }

}
