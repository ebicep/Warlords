package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabaseBasePvEEventNarmersTomb;

public class DatabaseMagePvEEventNarmersTomb extends DatabaseBasePvEEventNarmersTomb implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventNarmersTomb pyromancer = new DatabaseBasePvEEventNarmersTomb();
    protected DatabaseBasePvEEventNarmersTomb cryomancer = new DatabaseBasePvEEventNarmersTomb();
    protected DatabaseBasePvEEventNarmersTomb aquamancer = new DatabaseBasePvEEventNarmersTomb();

    public DatabaseMagePvEEventNarmersTomb() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventNarmersTomb[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventNarmersTomb getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventNarmersTomb getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventNarmersTomb getAquamancer() {
        return aquamancer;
    }

}
