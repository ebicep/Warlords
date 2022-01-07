package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseMageCTF extends DatabaseBaseCTF implements DatabaseWarlordsClass {

    protected DatabaseBaseCTF pyromancer = new DatabaseBaseCTF();
    protected DatabaseBaseCTF cryomancer = new DatabaseBaseCTF();
    protected DatabaseBaseCTF aquamancer = new DatabaseBaseCTF();

    public DatabaseMageCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseCTF[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBaseCTF getPyromancer() {
        return pyromancer;
    }

    public DatabaseBaseCTF getCryomancer() {
        return cryomancer;
    }

    public DatabaseBaseCTF getAquamancer() {
        return aquamancer;
    }

}
