package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public interface DatabaseWarlordsClasses<DatabaseGameT extends DatabaseGameBase,
        DatabaseGamePlayerT extends DatabaseGamePlayerBase,
        T extends Stats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends DatabaseWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>> {

    T getSpec(Specializations specializations);

    R getClass(Classes classes);

    R[] getClasses();

    R getMage();


    default T getPyromancer() {
        return getMage().getSpecs()[0];
    }

    default T getCryomancer() {
        return getMage().getSpecs()[1];
    }

    default T getAquamancer() {
        return getMage().getSpecs()[2];
    }

    R getWarrior();

    default T getBerserker() {
        return getWarrior().getSpecs()[0];
    }

    default T getDefender() {
        return getWarrior().getSpecs()[1];
    }

    default T getRevenant() {
        return getWarrior().getSpecs()[2];
    }

    R getPaladin();

    default T getAvenger() {
        return getPaladin().getSpecs()[0];
    }

    default T getCrusader() {
        return getPaladin().getSpecs()[1];
    }

    default T getProtector() {
        return getPaladin().getSpecs()[2];
    }

    R getShaman();

    default T getThunderlord() {
        return getShaman().getSpecs()[0];
    }

    default T getSpiritguard() {
        return getShaman().getSpecs()[1];
    }

    default T getEarthwarden() {
        return getShaman().getSpecs()[2];
    }

    R getRogue();

    default T getAssassin() {
        return getRogue().getSpecs()[0];
    }

    default T getVindicator() {
        return getRogue().getSpecs()[1];
    }

    default T getApothecary() {
        return getRogue().getSpecs()[2];
    }

    R getArcanist();

    default T getConjurer() {
        return getArcanist().getSpecs()[0];
    }

    default T getSentinel() {
        return getArcanist().getSpecs()[1];
    }

    default T getLuminary() {
        return getArcanist().getSpecs()[2];
    }

}
