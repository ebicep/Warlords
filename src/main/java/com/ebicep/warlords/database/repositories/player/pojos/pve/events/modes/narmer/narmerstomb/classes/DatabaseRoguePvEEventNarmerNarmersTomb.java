package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.PvEEventNarmerNarmersTombStatsWarlordsSpecs;

public class DatabaseRoguePvEEventNarmerNarmersTomb implements PvEEventNarmerNarmersTombStatsWarlordsSpecs {

    private DatabaseBasePvEEventNarmerNarmersTomb assassin = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb vindicator = new DatabaseBasePvEEventNarmerNarmersTomb();
    private DatabaseBasePvEEventNarmerNarmersTomb apothecary = new DatabaseBasePvEEventNarmerNarmersTomb();

    public DatabaseRoguePvEEventNarmerNarmersTomb() {
        super();
    }

    @Override
    public DatabaseBasePvEEventNarmerNarmersTomb[] getSpecs() {
        return new DatabaseBasePvEEventNarmerNarmersTomb[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventNarmerNarmersTomb getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getApothecary() {
        return apothecary;
    }
}
