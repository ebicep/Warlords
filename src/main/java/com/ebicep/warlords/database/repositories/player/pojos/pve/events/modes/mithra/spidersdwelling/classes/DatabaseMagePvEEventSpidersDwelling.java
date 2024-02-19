package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

public class DatabaseMagePvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventSpidersDwelling pyromancer = new DatabaseBasePvEEventSpidersDwelling();
    protected DatabaseBasePvEEventSpidersDwelling cryomancer = new DatabaseBasePvEEventSpidersDwelling();
    protected DatabaseBasePvEEventSpidersDwelling aquamancer = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseMagePvEEventSpidersDwelling() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventSpidersDwelling[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventSpidersDwelling getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventSpidersDwelling getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventSpidersDwelling getAquamancer() {
        return aquamancer;
    }

}
