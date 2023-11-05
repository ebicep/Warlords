package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabaseBaseSiege;

public class DatabaseRogueSiege extends DatabaseBaseSiege implements DatabaseWarlordsSpecs {

    private DatabaseBaseSiege assassin = new DatabaseBaseSiege();
    private DatabaseBaseSiege vindicator = new DatabaseBaseSiege();
    private DatabaseBaseSiege apothecary = new DatabaseBaseSiege();

    public DatabaseRogueSiege() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
