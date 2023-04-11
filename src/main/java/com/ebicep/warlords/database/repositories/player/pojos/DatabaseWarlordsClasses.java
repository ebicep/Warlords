package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public interface DatabaseWarlordsClasses<T extends AbstractDatabaseStatInformation> {

    T getSpec(Specializations specializations);

    T getClass(Classes classes);

    T[] getClasses();

    T getMage();

    T getWarrior();

    T getPaladin();

    T getShaman();

    T getRogue();

}
