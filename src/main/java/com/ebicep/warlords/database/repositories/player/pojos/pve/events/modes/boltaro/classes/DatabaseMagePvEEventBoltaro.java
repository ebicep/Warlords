package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

import java.util.List;

public class DatabaseMagePvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventBoltaro pyromancer = new DatabaseBasePvEEventBoltaro();
    protected DatabaseBasePvEEventBoltaro cryomancer = new DatabaseBasePvEEventBoltaro();
    protected DatabaseBasePvEEventBoltaro aquamancer = new DatabaseBasePvEEventBoltaro();

    public DatabaseMagePvEEventBoltaro() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventBoltaro getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventBoltaro getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventBoltaro getAquamancer() {
        return aquamancer;
    }

}
