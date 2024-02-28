package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtStatsWarlordsSpecs;

public class DatabaseShamanPvEOnslaught implements OnslaughtStatsWarlordsSpecs {

    private DatabaseBasePvEOnslaught thunderlord = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught spiritguard = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught earthwarden = new DatabaseBasePvEOnslaught();

    public DatabaseShamanPvEOnslaught() {
        super();
    }

    @Override
    public DatabaseBasePvEOnslaught[] getSpecs() {
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
