package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

public class DatabaseMagePvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEOnslaught pyromancer = new DatabaseBasePvEOnslaught();
    protected DatabaseBasePvEOnslaught cryomancer = new DatabaseBasePvEOnslaught();
    protected DatabaseBasePvEOnslaught aquamancer = new DatabaseBasePvEOnslaught();

    public DatabaseMagePvEOnslaught() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEOnslaught getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEOnslaught getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEOnslaught getAquamancer() {
        return aquamancer;
    }

}
