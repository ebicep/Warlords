package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabaseMageCTF extends AbstractDatabaseWarlordsClassCTF {

    protected DatabaseSpecializationCTF pyromancer = new DatabaseSpecializationCTF();
    protected DatabaseSpecializationCTF cryomancer = new DatabaseSpecializationCTF();
    protected DatabaseSpecializationCTF aquamancer = new DatabaseSpecializationCTF();

    public DatabaseMageCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseSpecializationCTF[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseSpecializationCTF getPyromancer() {
        return pyromancer;
    }

    public DatabaseSpecializationCTF getCryomancer() {
        return cryomancer;
    }

    public DatabaseSpecializationCTF getAquamancer() {
        return aquamancer;
    }

}
