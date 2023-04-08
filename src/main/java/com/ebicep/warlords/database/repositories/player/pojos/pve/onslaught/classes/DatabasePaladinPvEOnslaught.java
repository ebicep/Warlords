package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

public class DatabasePaladinPvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsClass {

    private DatabaseBasePvEOnslaught avenger = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught crusader = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught protector = new DatabaseBasePvEOnslaught();

    public DatabasePaladinPvEOnslaught() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEOnslaught getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEOnslaught getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEOnslaught getProtector() {
        return protector;
    }

}
