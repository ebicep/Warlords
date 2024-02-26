package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;

public interface StatsWarlordsSpecs<
        DatabaseGameT extends DatabaseGameBase<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        T extends Stats<DatabaseGameT, DatabaseGamePlayerT>>
        extends Stats<DatabaseGameT, DatabaseGamePlayerT>, DatabaseWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T> {

    @Override
    default void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameT databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerT gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {

    }

//    @Nonnull
//    private <NumT> NumT getStat(Function<Stats<DatabaseGameT, DatabaseGamePlayerT>, NumT> statFunction, BinaryOperator<NumT> accumulator, NumT defaultValue) {
//        return Arrays.stream(getSpecs())
//                     .map(statFunction)
//                     .reduce(accumulator)
//                     .orElse(defaultValue);
//    }

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

}
