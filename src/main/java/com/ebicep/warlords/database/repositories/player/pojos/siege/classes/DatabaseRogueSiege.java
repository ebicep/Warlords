package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;

import com.ebicep.warlords.database.repositories.player.pojos.siege.SiegeStatsWarlordsSpecs;

import java.util.List;

public class DatabaseRogueSiege implements SiegeStatsWarlordsSpecs {

    private DatabaseBaseSiege assassin = new DatabaseBaseSiege();
    private DatabaseBaseSiege vindicator = new DatabaseBaseSiege();
    private DatabaseBaseSiege apothecary = new DatabaseBaseSiege();

    public DatabaseRogueSiege() {
        super();
    }

    @Override
    public List<List<DatabaseBaseSiege>> getSpecs() {
        return new DatabaseBaseSiege[]{assassin, vindicator, apothecary};
    }


    public DatabaseBaseSiege getAssassin() {
        return assassin;
    }

    public DatabaseBaseSiege getVindicator() {
        return vindicator;
    }

    public DatabaseBaseSiege getApothecary() {
        return apothecary;
    }
}
