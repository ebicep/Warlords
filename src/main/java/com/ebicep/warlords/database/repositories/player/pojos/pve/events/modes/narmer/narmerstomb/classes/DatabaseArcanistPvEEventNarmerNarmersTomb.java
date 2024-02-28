package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.PvEEventNarmerNarmersTombStatsWarlordsSpecs;

public class DatabaseArcanistPvEEventNarmerNarmersTomb implements PvEEventNarmerNarmersTombStatsWarlordsSpecs {

    private DatabaseBasePvEEventNarmerNarmersTomb conjurer = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb sentinel = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb luminary = new DatabaseBasePvEEventNarmerNarmersTomb();

    public DatabaseArcanistPvEEventNarmerNarmersTomb() {
        super();
    }

    @Override
    public DatabaseBasePvEEventNarmerNarmersTomb[] getSpecs() {
        return new DatabaseBasePvEEventNarmerNarmersTomb[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventNarmerNarmersTomb getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getLuminary() {
        return luminary;
    }

}
