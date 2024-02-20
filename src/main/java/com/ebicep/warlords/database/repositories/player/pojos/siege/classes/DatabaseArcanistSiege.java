package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;


import com.ebicep.warlords.database.repositories.player.pojos.siege.SiegeStatsWarlordsSpecs;

import java.util.List;

public class DatabaseArcanistSiege implements SiegeStatsWarlordsSpecs {

    private DatabaseBaseSiege conjurer = new DatabaseBaseSiege();
    private DatabaseBaseSiege sentinel = new DatabaseBaseSiege();
    private DatabaseBaseSiege luminary = new DatabaseBaseSiege();

    public DatabaseArcanistSiege() {
        super();
    }

    @Override
    public List<List<DatabaseBaseSiege>> getSpecs() {
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
