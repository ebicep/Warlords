package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

public class DatabaseShamanPvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsClass {

    private DatabaseBasePvEOnslaught thunderlord = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught spiritguard = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught earthwarden = new DatabaseBasePvEOnslaught();

    public DatabaseShamanPvEOnslaught() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEOnslaught getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEOnslaught getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEOnslaught getEarthwarden() {
        return earthwarden;
    }

}
