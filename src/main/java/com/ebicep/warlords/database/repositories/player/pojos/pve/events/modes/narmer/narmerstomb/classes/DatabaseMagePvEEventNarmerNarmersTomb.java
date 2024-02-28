package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.PvEEventNarmerNarmersTombStatsWarlordsSpecs;

public class DatabaseMagePvEEventNarmerNarmersTomb implements PvEEventNarmerNarmersTombStatsWarlordsSpecs {

    protected DatabaseBasePvEEventNarmerNarmersTomb pyromancer = new DatabaseBasePvEEventNarmerNarmersTomb();
    protected DatabaseBasePvEEventNarmerNarmersTomb cryomancer = new DatabaseBasePvEEventNarmerNarmersTomb();
    protected DatabaseBasePvEEventNarmerNarmersTomb aquamancer = new DatabaseBasePvEEventNarmerNarmersTomb();

    public DatabaseMagePvEEventNarmerNarmersTomb() {
        super();
    }

    @Override
    public DatabaseBasePvEEventNarmerNarmersTomb[] getSpecs() {
        return new DatabaseBasePvEEventNarmerNarmersTomb[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventNarmerNarmersTomb getAquamancer() {
        return aquamancer;
    }

}
