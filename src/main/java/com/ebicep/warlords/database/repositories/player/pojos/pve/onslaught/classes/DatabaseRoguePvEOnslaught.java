package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

public class DatabaseRoguePvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsClass {

    private DatabaseBasePvEOnslaught assassin = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught vindicator = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught apothecary = new DatabaseBasePvEOnslaught();

    public DatabaseRoguePvEOnslaught() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEOnslaught getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEOnslaught getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEOnslaught getApothecary() {
        return apothecary;
    }
}
