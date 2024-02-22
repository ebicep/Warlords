package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface StatsWarlordsClasses<
        DatabaseGameT extends DatabaseGameBase<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        T extends Stats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends DatabaseWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>>
        extends Stats<DatabaseGameT, DatabaseGamePlayerT>, DatabaseWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, T, R> {

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
        getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
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

    @Override
    default T getSpec(Specializations specializations) {
        return switch (specializations) {
            case PYROMANCER -> getPyromancer();
            case CRYOMANCER -> getCryomancer();
            case AQUAMANCER -> getAquamancer();
            case BERSERKER -> getBerserker();
            case DEFENDER -> getDefender();
            case REVENANT -> getRevenant();
            case AVENGER -> getAvenger();
            case CRUSADER -> getCrusader();
            case PROTECTOR -> getProtector();
            case THUNDERLORD -> getThunderlord();
            case SPIRITGUARD -> getSpiritguard();
            case EARTHWARDEN -> getEarthwarden();
            case ASSASSIN -> getAssassin();
            case VINDICATOR -> getVindicator();
            case APOTHECARY -> getApothecary();
            case CONJURER -> getConjurer();
            case SENTINEL -> getSentinel();
            case LUMINARY -> getLuminary();
        };
    }

    @Override
    default R[] getClasses() {
        return (R[]) new DatabaseWarlordsSpecs[]{getMage(), getWarrior(), getPaladin(), getShaman(), getRogue(), getArcanist()};
    }

    @Override
    default R getMage() {
        return getClass(Classes.MAGE);
    }

    @Override
    default R getWarrior() {
        return getClass(Classes.WARRIOR);
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
    default R getRogue() {
        return getClass(Classes.ROGUE);
    }

    @Override
    default R getArcanist() {
        return getClass(Classes.ARCANIST);
    }

    @Nonnull
    default <NumT> NumT getStat(Function<T, NumT> statFunction, BinaryOperator<NumT> accumulator, NumT defaultValue) {
        return Arrays.stream(Specializations.VALUES)
                     .map(spec -> statFunction.apply(getSpec(spec)))
                     .reduce(accumulator)
                     .orElse(defaultValue);
    }

}
