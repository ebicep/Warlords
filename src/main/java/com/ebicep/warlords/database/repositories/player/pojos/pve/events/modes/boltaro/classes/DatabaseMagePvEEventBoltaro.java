package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabaseMagePvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventBoltaro pyromancer = new DatabaseBasePvEEventBoltaro();
    protected DatabaseBasePvEEventBoltaro cryomancer = new DatabaseBasePvEEventBoltaro();
    protected DatabaseBasePvEEventBoltaro aquamancer = new DatabaseBasePvEEventBoltaro();

    public DatabaseMagePvEEventBoltaro() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
