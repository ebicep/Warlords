package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseShamanTDM extends DatabaseBaseTDM implements DatabaseWarlordsSpecs {

    private DatabaseBaseTDM thunderlord = new DatabaseBaseTDM();
    private DatabaseBaseTDM spiritguard = new DatabaseBaseTDM();
    private DatabaseBaseTDM earthwarden = new DatabaseBaseTDM();

    public DatabaseShamanTDM() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
