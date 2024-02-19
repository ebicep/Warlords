package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabaseBasePvEEventMithra;

public class DatabaseMagePvEEventMithra extends DatabaseBasePvEEventMithra implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventMithra pyromancer = new DatabaseBasePvEEventMithra();
    protected DatabaseBasePvEEventMithra cryomancer = new DatabaseBasePvEEventMithra();
    protected DatabaseBasePvEEventMithra aquamancer = new DatabaseBasePvEEventMithra();

    public DatabaseMagePvEEventMithra() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventMithra[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventMithra getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventMithra getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventMithra getAquamancer() {
        return aquamancer;
    }

}
