package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabaseBasePvEEventGardenOfHesperidesTartarus;

public class DatabaseMagePvEEventGardenOfHesperidesTartarus extends DatabaseBasePvEEventGardenOfHesperidesTartarus implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventGardenOfHesperidesTartarus pyromancer = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    protected DatabaseBasePvEEventGardenOfHesperidesTartarus cryomancer = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    protected DatabaseBasePvEEventGardenOfHesperidesTartarus aquamancer = new DatabaseBasePvEEventGardenOfHesperidesTartarus();

    public DatabaseMagePvEEventGardenOfHesperidesTartarus() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesTartarus[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getAquamancer() {
        return aquamancer;
    }

}
