package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

import java.util.List;

public interface DatabaseWarlordsClasses<DatabaseGameT extends DatabaseGameBase,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        T extends Stats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends DatabaseWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>> {

    List<T> getSpec(Specializations specializations);

    List<R> getClass(Classes classes);

    List<List<R>> getClasses();

    List<R> getMage();

    private List<T> getSpec(List<R> classes, int index) {
        return classes.stream().map(r -> r.getSpecs()[index]).toList();
    }

    default List<T> getPyromancer() {
        return getSpec(getMage(), 0);
    }

    default List<T> getCryomancer() {
        return getSpec(getMage(), 1);
    }

    default List<T> getAquamancer() {
        return getSpec(getMage(), 2);
    }

    List<R> getWarrior();

    default List<T> getBerserker() {
        return getSpec(getWarrior(), 0);
    }

    default List<T> getDefender() {
        return getSpec(getWarrior(), 1);
    }

    default List<T> getRevenant() {
        return getSpec(getWarrior(), 2);
    }

    List<R> getPaladin();

    default List<T> getAvenger() {
        return getSpec(getPaladin(), 0);
    }

    default List<T> getCrusader() {
        return getSpec(getPaladin(), 1);
    }

    default List<T> getProtector() {
        return getSpec(getPaladin(), 2);
    }

    List<R> getShaman();

    default List<T> getThunderlord() {
        return getSpec(getShaman(), 0);
    }

    default List<T> getSpiritguard() {
        return getSpec(getShaman(), 1);
    }

    default List<T> getEarthwarden() {
        return getSpec(getShaman(), 2);
    }

    List<R> getRogue();

    default List<T> getAssassin() {
        return getSpec(getRogue(), 0);
    }

    default List<T> getVindicator() {
        return getSpec(getRogue(), 1);
    }

    default List<T> getApothecary() {
        return getSpec(getRogue(), 2);
    }

    List<R> getArcanist();

    default List<T> getConjurer() {
        return getSpec(getArcanist(), 0);
    }

    default List<T> getSentinel() {
        return getSpec(getArcanist(), 1);
    }

    default List<T> getLuminary() {
        return getSpec(getArcanist(), 2);
    }

}
