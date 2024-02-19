package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabasePaladinTDM extends DatabaseBaseTDM implements DatabaseWarlordsSpecs {

    private DatabaseBaseTDM avenger = new DatabaseBaseTDM();
    private DatabaseBaseTDM crusader = new DatabaseBaseTDM();
    private DatabaseBaseTDM protector = new DatabaseBaseTDM();

    public DatabasePaladinTDM() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBaseTDM[]{avenger, crusader, protector};
    }

    public DatabaseBaseTDM getAvenger() {
        return avenger;
    }

    public DatabaseBaseTDM getCrusader() {
        return crusader;
    }

    public DatabaseBaseTDM getProtector() {
        return protector;
    }

}
