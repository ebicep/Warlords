package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

import java.util.List;

public class DatabaseShamanPvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEOnslaught thunderlord = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught spiritguard = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught earthwarden = new DatabaseBasePvEOnslaught();

    public DatabaseShamanPvEOnslaught() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
