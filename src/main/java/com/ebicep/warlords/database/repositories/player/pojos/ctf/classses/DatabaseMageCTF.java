package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.ctf.CTFStatsWarlordsSpecs;

public class DatabaseMageCTF implements CTFStatsWarlordsSpecs {

    protected DatabaseBaseCTF pyromancer = new DatabaseBaseCTF();
    protected DatabaseBaseCTF cryomancer = new DatabaseBaseCTF();
    protected DatabaseBaseCTF aquamancer = new DatabaseBaseCTF();

    public DatabaseMageCTF() {
        super();
    }

    @Override
    public DatabaseBaseCTF[] getSpecs() {
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
