package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabaseBasePvEEventGardenOfHesperides;

public class DatabaseMagePvEEventGardenOfHesperides extends DatabaseBasePvEEventGardenOfHesperides implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventGardenOfHesperides pyromancer = new DatabaseBasePvEEventGardenOfHesperides();
    protected DatabaseBasePvEEventGardenOfHesperides cryomancer = new DatabaseBasePvEEventGardenOfHesperides();
    protected DatabaseBasePvEEventGardenOfHesperides aquamancer = new DatabaseBasePvEEventGardenOfHesperides();

    public DatabaseMagePvEEventGardenOfHesperides() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperides[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventGardenOfHesperides getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventGardenOfHesperides getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventGardenOfHesperides getAquamancer() {
        return aquamancer;
    }

}
