package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabaseBaseSiege;

public class DatabaseRogueSiege implements StatsWarlordsSpecs<DatabaseBaseSiege> {

    private DatabaseBaseSiege assassin = new DatabaseBaseSiege();
    private DatabaseBaseSiege vindicator = new DatabaseBaseSiege();
    private DatabaseBaseSiege apothecary = new DatabaseBaseSiege();

    public DatabaseRogueSiege() {
        super();
    }

    @Override
    public DatabaseBaseSiege[] getSpecs() {
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
