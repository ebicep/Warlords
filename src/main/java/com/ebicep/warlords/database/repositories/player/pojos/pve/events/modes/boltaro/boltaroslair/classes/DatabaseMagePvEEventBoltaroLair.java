package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.PvEEventBoltaroLairStatsWarlordsSpecs;

public class DatabaseMagePvEEventBoltaroLair implements PvEEventBoltaroLairStatsWarlordsSpecs {

    protected DatabaseBasePvEEventBoltaroLair pyromancer = new DatabaseBasePvEEventBoltaroLair();
    protected DatabaseBasePvEEventBoltaroLair cryomancer = new DatabaseBasePvEEventBoltaroLair();
    protected DatabaseBasePvEEventBoltaroLair aquamancer = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseMagePvEEventBoltaroLair() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroLair[] getSpecs() {
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
