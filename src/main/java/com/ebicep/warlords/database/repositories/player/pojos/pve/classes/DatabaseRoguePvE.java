package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

public class DatabaseRoguePvE implements PvEStatsWarlordsSpecs<DatabaseGamePvEBase, DatabaseGamePlayerPvEBase, DatabaseBasePvE> {

    private DatabaseBasePvE assassin = new DatabaseBasePvE();
    private DatabaseBasePvE vindicator = new DatabaseBasePvE();
    private DatabaseBasePvE apothecary = new DatabaseBasePvE();

    public DatabaseRoguePvE() {
        super();
    }

    @Override
    public DatabaseBasePvE[] getSpecs() {
        return new DatabaseBasePvE[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvE getAssassin() {
        return assassin;
    }

    public DatabaseBasePvE getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvE getApothecary() {
        return apothecary;
    }
}
