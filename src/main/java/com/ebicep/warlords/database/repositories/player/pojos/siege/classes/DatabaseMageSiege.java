package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;

import com.ebicep.warlords.database.repositories.player.pojos.siege.SiegeStatsWarlordsSpecs;

public class DatabaseMageSiege implements SiegeStatsWarlordsSpecs {

    protected DatabaseBaseSiege pyromancer = new DatabaseBaseSiege();
    protected DatabaseBaseSiege cryomancer = new DatabaseBaseSiege();
    protected DatabaseBaseSiege aquamancer = new DatabaseBaseSiege();

    public DatabaseMageSiege() {
        super();
    }

    @Override
    public DatabaseBaseSiege[] getSpecs() {
        return new DatabaseBaseSiege[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBaseSiege getPyromancer() {
        return pyromancer;
    }

    public DatabaseBaseSiege getCryomancer() {
        return cryomancer;
    }

    public DatabaseBaseSiege getAquamancer() {
        return aquamancer;
    }

}
