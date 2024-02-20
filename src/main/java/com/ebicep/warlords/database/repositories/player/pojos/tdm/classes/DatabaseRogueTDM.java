package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.tdm.TDMStatsWarlordsSpecs;

import java.util.List;

public class DatabaseRogueTDM implements TDMStatsWarlordsSpecs {

    private DatabaseBaseTDM assassin = new DatabaseBaseTDM();
    private DatabaseBaseTDM vindicator = new DatabaseBaseTDM();
    private DatabaseBaseTDM apothecary = new DatabaseBaseTDM();

    public DatabaseRogueTDM() {
        super();
    }

    @Override
    public List<List<DatabaseBaseTDM>> getSpecs() {
        return new DatabaseBaseTDM[]{assassin, vindicator, apothecary};
    }


    public DatabaseBaseTDM getAssassin() {
        return assassin;
    }

    public DatabaseBaseTDM getVindicator() {
        return vindicator;
    }

    public DatabaseBaseTDM getApothecary() {
        return apothecary;
    }
}
