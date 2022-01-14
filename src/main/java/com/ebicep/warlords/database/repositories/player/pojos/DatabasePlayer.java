package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ClassesGroup;

public interface DatabasePlayer {

    AbstractDatabaseStatInformation getSpec(Classes classes);

    AbstractDatabaseStatInformation getClass(ClassesGroup classesGroup);

    AbstractDatabaseStatInformation[] getClasses();

}
