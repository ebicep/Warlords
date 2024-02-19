package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

public class DatabaseShamanPvE implements PvEStatsWarlordsSpecs<DatabaseBasePvE> {

    private DatabaseBasePvE thunderlord = new DatabaseBasePvE();
    private DatabaseBasePvE spiritguard = new DatabaseBasePvE();
    private DatabaseBasePvE earthwarden = new DatabaseBasePvE();

    public DatabaseShamanPvE() {
        super();
    }

    @Override
    public DatabaseBasePvE[] getSpecs() {
        return new DatabaseBasePvE[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvE getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvE getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvE getEarthwarden() {
        return earthwarden;
    }

}
