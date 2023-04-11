package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;

public class DatabaseMagePvE extends DatabaseBasePvE implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvE pyromancer = new DatabaseBasePvE();
    protected DatabaseBasePvE cryomancer = new DatabaseBasePvE();
    protected DatabaseBasePvE aquamancer = new DatabaseBasePvE();

    public DatabaseMagePvE() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvE[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvE getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvE getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvE getAquamancer() {
        return aquamancer;
    }

}
