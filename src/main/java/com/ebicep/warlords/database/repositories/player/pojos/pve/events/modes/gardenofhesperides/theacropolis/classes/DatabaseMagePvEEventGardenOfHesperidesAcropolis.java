package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs;

public class DatabaseMagePvEEventGardenOfHesperidesAcropolis implements PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs {

    protected DatabaseBasePvEEventGardenOfHesperidesAcropolis pyromancer = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    protected DatabaseBasePvEEventGardenOfHesperidesAcropolis cryomancer = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    protected DatabaseBasePvEEventGardenOfHesperidesAcropolis aquamancer = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();

    public DatabaseMagePvEEventGardenOfHesperidesAcropolis() {
        super();
    }

    @Override
    public DatabaseBasePvEEventGardenOfHesperidesAcropolis[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesAcropolis[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getAquamancer() {
        return aquamancer;
    }

}
