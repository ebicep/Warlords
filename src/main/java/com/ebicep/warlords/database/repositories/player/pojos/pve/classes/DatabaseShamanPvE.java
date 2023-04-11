package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;

public class DatabaseShamanPvE extends DatabaseBasePvE implements DatabaseWarlordsSpecs {

    private DatabaseBasePvE thunderlord = new DatabaseBasePvE();
    private DatabaseBasePvE spiritguard = new DatabaseBasePvE();
    private DatabaseBasePvE earthwarden = new DatabaseBasePvE();

    public DatabaseShamanPvE() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvE[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvE getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvE getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvE getEarthwarden() {
        return earthwarden;
    }

}
