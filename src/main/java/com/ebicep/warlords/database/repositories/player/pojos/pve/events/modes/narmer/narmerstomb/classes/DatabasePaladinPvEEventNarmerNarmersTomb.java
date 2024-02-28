package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.PvEEventNarmerNarmersTombStatsWarlordsSpecs;

public class DatabasePaladinPvEEventNarmerNarmersTomb implements PvEEventNarmerNarmersTombStatsWarlordsSpecs {

    private DatabaseBasePvEEventNarmerNarmersTomb avenger = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb crusader = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb protector = new DatabaseBasePvEEventNarmerNarmersTomb();

    public DatabasePaladinPvEEventNarmerNarmersTomb() {
        super();
    }

    @Override
    public DatabaseBasePvEEventNarmerNarmersTomb[] getSpecs() {
        return new DatabaseBasePvEEventNarmerNarmersTomb[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getProtector() {
        return protector;
    }

}
