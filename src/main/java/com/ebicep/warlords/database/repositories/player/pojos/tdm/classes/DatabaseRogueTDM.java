package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseRogueTDM extends DatabaseBaseTDM implements DatabaseWarlordsSpecs {

    private DatabaseBaseTDM assassin = new DatabaseBaseTDM();
    private DatabaseBaseTDM vindicator = new DatabaseBaseTDM();
    private DatabaseBaseTDM apothecary = new DatabaseBaseTDM();

    public DatabaseRogueTDM() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
