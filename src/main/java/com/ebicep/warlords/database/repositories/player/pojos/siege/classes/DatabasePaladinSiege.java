package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;


import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabaseBaseSiege;

public class DatabasePaladinSiege implements StatsWarlordsSpecs<DatabaseBaseSiege> {

    private DatabaseBaseSiege avenger = new DatabaseBaseSiege();
    private DatabaseBaseSiege crusader = new DatabaseBaseSiege();
    private DatabaseBaseSiege protector = new DatabaseBaseSiege();

    public DatabasePaladinSiege() {
        super();
    }

    @Override
    public DatabaseBaseSiege[] getSpecs() {
        return new DatabaseBaseSiege[]{avenger, crusader, protector};
    }


    public DatabaseBaseSiege getAvenger() {
        return avenger;
    }

    public DatabaseBaseSiege getCrusader() {
        return crusader;
    }

    public DatabaseBaseSiege getProtector() {
        return protector;
    }

}
