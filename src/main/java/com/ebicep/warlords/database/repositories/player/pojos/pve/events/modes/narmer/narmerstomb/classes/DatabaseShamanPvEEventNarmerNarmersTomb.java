package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.PvEEventNarmerNarmersTombStatsWarlordsSpecs;

public class DatabaseShamanPvEEventNarmerNarmersTomb implements PvEEventNarmerNarmersTombStatsWarlordsSpecs {

    private DatabaseBasePvEEventNarmerNarmersTomb thunderlord = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb spiritguard = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb earthwarden = new DatabaseBasePvEEventNarmerNarmersTomb();

    public DatabaseShamanPvEEventNarmerNarmersTomb() {
        super();
    }

    @Override
    public DatabaseBasePvEEventNarmerNarmersTomb[] getSpecs() {
        return new DatabaseBasePvEEventNarmerNarmersTomb[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getEarthwarden() {
        return earthwarden;
    }

}
