package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseShamanTDM extends DatabaseBaseTDM implements DatabaseWarlordsClass {

    private DatabaseBaseTDM thunderlord = new DatabaseBaseTDM();
    private DatabaseBaseTDM spiritguard = new DatabaseBaseTDM();
    private DatabaseBaseTDM earthwarden = new DatabaseBaseTDM();

    public DatabaseShamanTDM() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseTDM[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBaseTDM getThunderlord() {
        return thunderlord;
    }

    public DatabaseBaseTDM getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBaseTDM getEarthwarden() {
        return earthwarden;
    }

}
