package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.duel.DuelStatsWarlordsSpecs;

public class DatabaseMageDuel implements DuelStatsWarlordsSpecs {

    protected DatabaseBaseDuel pyromancer = new DatabaseBaseDuel();
    protected DatabaseBaseDuel cryomancer = new DatabaseBaseDuel();
    protected DatabaseBaseDuel aquamancer = new DatabaseBaseDuel();

    public DatabaseMageDuel() {
        super();
    }

    @Override
    public DatabaseBaseDuel[] getSpecs() {
        return new DatabaseBaseDuel[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBaseDuel getPyromancer() {
        return pyromancer;
    }

    public DatabaseBaseDuel getCryomancer() {
        return cryomancer;
    }

    public DatabaseBaseDuel getAquamancer() {
        return aquamancer;
    }

}
