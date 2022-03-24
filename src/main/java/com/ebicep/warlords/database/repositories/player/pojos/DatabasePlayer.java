package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.Specializations;

public interface DatabasePlayer {

    AbstractDatabaseStatInformation getSpec(Specializations specializations);

    AbstractDatabaseStatInformation getClass(Classes classes);

    AbstractDatabaseStatInformation[] getClasses();

}
