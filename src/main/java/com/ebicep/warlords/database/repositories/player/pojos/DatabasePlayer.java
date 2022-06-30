package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;

public interface DatabasePlayer {

    AbstractDatabaseStatInformation getSpec(Specializations specializations);

    AbstractDatabaseStatInformation getClass(Classes classes);

    AbstractDatabaseStatInformation[] getClasses();

}
