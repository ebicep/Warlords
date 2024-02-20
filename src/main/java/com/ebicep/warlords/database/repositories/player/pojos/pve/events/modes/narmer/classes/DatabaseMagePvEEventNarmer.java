package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

import java.util.List;

public class DatabaseMagePvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventNarmer pyromancer = new DatabaseBasePvEEventNarmer();
    protected DatabaseBasePvEEventNarmer cryomancer = new DatabaseBasePvEEventNarmer();
    protected DatabaseBasePvEEventNarmer aquamancer = new DatabaseBasePvEEventNarmer();

    public DatabaseMagePvEEventNarmer() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventNarmer getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventNarmer getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventNarmer getAquamancer() {
        return aquamancer;
    }

}
