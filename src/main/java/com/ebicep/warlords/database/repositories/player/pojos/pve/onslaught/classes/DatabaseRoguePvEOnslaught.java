package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtStatsWarlordsSpecs;

public class DatabaseRoguePvEOnslaught implements OnslaughtStatsWarlordsSpecs {

    private DatabaseBasePvEOnslaught assassin = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught vindicator = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught apothecary = new DatabaseBasePvEOnslaught();

    public DatabaseRoguePvEOnslaught() {
        super();
    }

    @Override
    public DatabaseBasePvEOnslaught[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEOnslaught getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEOnslaught getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEOnslaught getApothecary() {
        return apothecary;
    }
}
