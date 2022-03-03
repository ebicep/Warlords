package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseMageTDM extends DatabaseBaseTDM implements DatabaseWarlordsClass {

    protected DatabaseBaseTDM pyromancer = new DatabaseBaseTDM();
    protected DatabaseBaseTDM cryomancer = new DatabaseBaseTDM();
    protected DatabaseBaseTDM aquamancer = new DatabaseBaseTDM();

    public DatabaseMageTDM() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseTDM[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBaseTDM getPyromancer() {
        return pyromancer;
    }

    public DatabaseBaseTDM getCryomancer() {
        return cryomancer;
    }

    public DatabaseBaseTDM getAquamancer() {
        return aquamancer;
    }

}
