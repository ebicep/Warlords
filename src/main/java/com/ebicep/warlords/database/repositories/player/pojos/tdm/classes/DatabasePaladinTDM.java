package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;

import com.ebicep.warlords.database.repositories.player.pojos.tdm.TDMStatsWarlordsSpecs;

public class DatabasePaladinTDM implements TDMStatsWarlordsSpecs {

    private DatabaseBaseTDM avenger = new DatabaseBaseTDM();
    private DatabaseBaseTDM crusader = new DatabaseBaseTDM();
    private DatabaseBaseTDM protector = new DatabaseBaseTDM();

    public DatabasePaladinTDM() {
        super();
    }

    @Override
    public DatabaseBaseTDM[] getSpecs() {
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
