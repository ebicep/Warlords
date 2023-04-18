package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;

public class DatabaseRoguePvE extends DatabaseBasePvE implements DatabaseWarlordsSpecs {

    private DatabaseBasePvE assassin = new DatabaseBasePvE();
    private DatabaseBasePvE vindicator = new DatabaseBasePvE();
    private DatabaseBasePvE apothecary = new DatabaseBasePvE();

    public DatabaseRoguePvE() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvE[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvE getAssassin() {
        return assassin;
    }

    public DatabaseBasePvE getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvE getApothecary() {
        return apothecary;
    }
}
