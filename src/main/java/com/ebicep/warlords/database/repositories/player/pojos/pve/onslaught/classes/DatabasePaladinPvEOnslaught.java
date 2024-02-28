package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.OnslaughtStatsWarlordsSpecs;

public class DatabasePaladinPvEOnslaught implements OnslaughtStatsWarlordsSpecs {

    private DatabaseBasePvEOnslaught avenger = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught crusader = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught protector = new DatabaseBasePvEOnslaught();

    public DatabasePaladinPvEOnslaught() {
        super();
    }

    @Override
    public DatabaseBasePvEOnslaught[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEOnslaught getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEOnslaught getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEOnslaught getProtector() {
        return protector;
    }

}
