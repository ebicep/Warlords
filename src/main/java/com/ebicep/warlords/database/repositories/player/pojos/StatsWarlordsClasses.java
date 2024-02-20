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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public interface StatsWarlordsClasses<
        DatabaseGameT extends DatabaseGameBase,
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
        getSpec(gamePlayer.getSpec()).forEach(t -> t.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection));
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
    default void setExperience(long experience) {
        // only set for specs
    }

    @Nonnull
    default <NumT> NumT getStat(Function<T, NumT> statFunction, BinaryOperator<NumT> accumulator, NumT defaultValue) {
        return Arrays.stream(Specializations.VALUES)
                     .flatMap(spec -> getSpec(spec).stream())
                     .map(statFunction)
                     .reduce(accumulator)
                     .orElse(defaultValue);
    }

    @Override
    default List<T> getSpec(Specializations specializations) {
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
    default List<R> getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> getMage();
            case WARRIOR -> getWarrior();
            case PALADIN -> getPaladin();
            case SHAMAN -> getShaman();
            case ROGUE -> getRogue();
            case ARCANIST -> getArcanist();
        };
    }

    @Override
    default List<List<R>> getClasses() {
        List<List<R>> classes = new ArrayList<>();
        classes.add(getMage());
        classes.add(getWarrior());
        classes.add(getPaladin());
        classes.add(getShaman());
        classes.add(getRogue());
        classes.add(getArcanist());
        return classes;
    }

    @Override
    default List<R> getMage() {
        return getClass(Classes.MAGE);
    }

    @Override
    default List<R> getWarrior() {
        return getClass(Classes.WARRIOR);
    }

    @Override
    default List<R> getPaladin() {
        return getClass(Classes.PALADIN);
    }

    @Override
    default List<R> getShaman() {
        return getClass(Classes.SHAMAN);
    }

    @Override
    default List<R> getRogue() {
        return getClass(Classes.ROGUE);
    }

    @Override
    default List<R> getArcanist() {
        return getClass(Classes.ARCANIST);
    }

}
