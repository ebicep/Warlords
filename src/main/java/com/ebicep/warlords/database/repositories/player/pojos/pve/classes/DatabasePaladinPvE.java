package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;

public class DatabasePaladinPvE extends DatabaseBasePvE implements DatabaseWarlordsSpecs {

    private DatabaseBasePvE avenger = new DatabaseBasePvE();
    private DatabaseBasePvE crusader = new DatabaseBasePvE();
    private DatabaseBasePvE protector = new DatabaseBasePvE();

    public DatabasePaladinPvE() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvE[]{avenger, crusader, protector};
    }

    public DatabaseBasePvE getAvenger() {
        return avenger;
    }

    public DatabaseBasePvE getCrusader() {
        return crusader;
    }

    public DatabaseBasePvE getProtector() {
        return protector;
    }

}
