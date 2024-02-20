package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

public class DatabasePaladinPvE implements PvEStatsWarlordsSpecs<DatabaseGamePvEBase, DatabaseGamePlayerPvEBase, DatabaseBasePvE> {

    private DatabaseBasePvE avenger = new DatabaseBasePvE();
    private DatabaseBasePvE crusader = new DatabaseBasePvE();
    private DatabaseBasePvE protector = new DatabaseBasePvE();

    public DatabasePaladinPvE() {
        super();
    }

    @Override
    public DatabaseBasePvE[] getSpecs() {
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
