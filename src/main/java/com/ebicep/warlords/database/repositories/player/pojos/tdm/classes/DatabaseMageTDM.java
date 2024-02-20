package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.tdm.TDMStatsWarlordsSpecs;

import java.util.List;

public class DatabaseMageTDM implements TDMStatsWarlordsSpecs {

    protected DatabaseBaseTDM pyromancer = new DatabaseBaseTDM();
    protected DatabaseBaseTDM cryomancer = new DatabaseBaseTDM();
    protected DatabaseBaseTDM aquamancer = new DatabaseBaseTDM();

    public DatabaseMageTDM() {
        super();
    }

    @Override
    public List<List<DatabaseBaseTDM>> getSpecs() {
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
