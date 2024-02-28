package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;


import com.ebicep.warlords.database.repositories.player.pojos.siege.SiegeStatsWarlordsSpecs;

public class DatabasePaladinSiege implements SiegeStatsWarlordsSpecs {

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
