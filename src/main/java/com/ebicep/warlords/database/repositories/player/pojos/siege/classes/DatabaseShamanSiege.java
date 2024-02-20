package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;

import com.ebicep.warlords.database.repositories.player.pojos.siege.SiegeStatsWarlordsSpecs;

public class DatabaseShamanSiege implements SiegeStatsWarlordsSpecs {

    private DatabaseBaseSiege thunderlord = new DatabaseBaseSiege();
    private DatabaseBaseSiege spiritguard = new DatabaseBaseSiege();
    private DatabaseBaseSiege earthwarden = new DatabaseBaseSiege();

    public DatabaseShamanSiege() {
        super();
    }

    @Override
    public DatabaseBaseSiege[] getSpecs() {
        return new DatabaseBaseSiege[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBaseSiege getThunderlord() {
        return thunderlord;
    }

    public DatabaseBaseSiege getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBaseSiege getEarthwarden() {
        return earthwarden;
    }

}
