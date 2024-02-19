package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseMageCTF implements StatsWarlordsSpecs<DatabaseBaseCTF> {

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
