package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabaseBaseSiege;

public class DatabasePaladinSiege extends DatabaseBaseSiege implements DatabaseWarlordsSpecs {

    private DatabaseBaseSiege avenger = new DatabaseBaseSiege();
    private DatabaseBaseSiege crusader = new DatabaseBaseSiege();
    private DatabaseBaseSiege protector = new DatabaseBaseSiege();

    public DatabasePaladinSiege() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseSiege[]{avenger, crusader, protector};
    }


    public DatabaseBaseSiege getAvenger() {
        return avenger;
    }

    public DatabaseBaseSiege getCrusader() {
        return crusader;
    }

    public DatabaseBaseSiege getProtector() {
        return protector;
    }

}
