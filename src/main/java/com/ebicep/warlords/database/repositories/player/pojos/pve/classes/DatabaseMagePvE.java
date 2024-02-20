package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

import java.util.List;

public class DatabaseMagePvE implements PvEStatsWarlordsSpecs<DatabaseBasePvE> {

    protected DatabaseBasePvE pyromancer = new DatabaseBasePvE();
    protected DatabaseBasePvE cryomancer = new DatabaseBasePvE();
    protected DatabaseBasePvE aquamancer = new DatabaseBasePvE();

    public DatabaseMagePvE() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
