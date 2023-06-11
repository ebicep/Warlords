package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

public class DatabaseDruidPvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmer conjurer = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer guardian = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer priest = new DatabaseBasePvEEventNarmer();

    public DatabaseDruidPvEEventNarmer() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEEventNarmer getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventNarmer getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEventNarmer getPriest() {
        return priest;
    }
}
