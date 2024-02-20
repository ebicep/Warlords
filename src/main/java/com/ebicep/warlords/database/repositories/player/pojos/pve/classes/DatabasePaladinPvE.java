package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

import java.util.List;

public class DatabasePaladinPvE implements PvEStatsWarlordsSpecs<DatabaseBasePvE> {

    private DatabaseBasePvE avenger = new DatabaseBasePvE();
    private DatabaseBasePvE crusader = new DatabaseBasePvE();
    private DatabaseBasePvE protector = new DatabaseBasePvE();

    public DatabasePaladinPvE() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvE[]{avenger, crusader, protector};
    }

    public DatabaseBasePvE getAvenger() {
        return avenger;
    }

    public DatabaseBasePvE getCrusader() {
        return crusader;
    }

    public DatabaseBasePvE getProtector() {
        return protector;
    }

}
