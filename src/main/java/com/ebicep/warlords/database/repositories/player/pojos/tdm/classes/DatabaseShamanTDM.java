package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.tdm.TDMStatsWarlordsSpecs;

import java.util.List;

public class DatabaseShamanTDM implements TDMStatsWarlordsSpecs {

    private DatabaseBaseTDM thunderlord = new DatabaseBaseTDM();
    private DatabaseBaseTDM spiritguard = new DatabaseBaseTDM();
    private DatabaseBaseTDM earthwarden = new DatabaseBaseTDM();

    public DatabaseShamanTDM() {
        super();
    }

    @Override
    public List<List<DatabaseBaseTDM>> getSpecs() {
        return new DatabaseBaseTDM[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBaseTDM getThunderlord() {
        return thunderlord;
    }

    public DatabaseBaseTDM getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBaseTDM getEarthwarden() {
        return earthwarden;
    }

}
