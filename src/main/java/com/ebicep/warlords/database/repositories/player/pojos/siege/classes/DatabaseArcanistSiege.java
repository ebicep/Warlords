package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;


import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabaseBaseSiege;

public class DatabaseArcanistSiege implements StatsWarlordsSpecs<DatabaseBaseSiege> {

    private DatabaseBaseSiege conjurer = new DatabaseBaseSiege();
    private DatabaseBaseSiege sentinel = new DatabaseBaseSiege();
    private DatabaseBaseSiege luminary = new DatabaseBaseSiege();

    public DatabaseArcanistSiege() {
        super();
    }

    @Override
    public DatabaseBaseSiege[] getSpecs() {
        return new DatabaseBaseSiege[]{conjurer, sentinel, luminary};
    }


    public DatabaseBaseSiege getConjurer() {
        return conjurer;
    }

    public DatabaseBaseSiege getSentinel() {
        return sentinel;
    }

    public DatabaseBaseSiege getLuminary() {
        return luminary;
    }

}
