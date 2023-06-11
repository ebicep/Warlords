package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabaseBasePvEEventMithra;

public class DatabaseDruidPvEEventMithra extends DatabaseBasePvEEventMithra implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventMithra conjurer = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra guardian = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra priest = new DatabaseBasePvEEventMithra();

    public DatabaseDruidPvEEventMithra() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventMithra[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEEventMithra getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventMithra getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEventMithra getPriest() {
        return priest;
    }

}
