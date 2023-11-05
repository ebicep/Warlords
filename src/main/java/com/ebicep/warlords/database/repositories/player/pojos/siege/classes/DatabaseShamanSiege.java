package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabaseBaseSiege;

public class DatabaseShamanSiege extends DatabaseBaseSiege implements DatabaseWarlordsSpecs {

    private DatabaseBaseSiege thunderlord = new DatabaseBaseSiege();
    private DatabaseBaseSiege spiritguard = new DatabaseBaseSiege();
    private DatabaseBaseSiege earthwarden = new DatabaseBaseSiege();

    public DatabaseShamanSiege() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
