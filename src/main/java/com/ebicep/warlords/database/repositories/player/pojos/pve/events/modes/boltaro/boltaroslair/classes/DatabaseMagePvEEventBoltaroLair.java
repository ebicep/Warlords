package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

public class DatabaseMagePvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventBoltaroLair pyromancer = new DatabaseBasePvEEventBoltaroLair();
    protected DatabaseBasePvEEventBoltaroLair cryomancer = new DatabaseBasePvEEventBoltaroLair();
    protected DatabaseBasePvEEventBoltaroLair aquamancer = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseMagePvEEventBoltaroLair() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventBoltaroLair getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventBoltaroLair getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventBoltaroLair getAquamancer() {
        return aquamancer;
    }

}
