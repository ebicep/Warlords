package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseRogueTDM extends DatabaseBaseTDM implements DatabaseWarlordsClass {

    private DatabaseBaseTDM assassin = new DatabaseBaseTDM();
    private DatabaseBaseTDM vindicator = new DatabaseBaseTDM();
    private DatabaseBaseTDM apothecary = new DatabaseBaseTDM();

    public DatabaseRogueTDM() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseTDM[]{assassin, vindicator, apothecary};
    }


    public DatabaseBaseTDM getAssassin() {
        return assassin;
    }

    public DatabaseBaseTDM getVindicator() {
        return vindicator;
    }

    public DatabaseBaseTDM getApothecary() {
        return apothecary;
    }
}
