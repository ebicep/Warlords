package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabaseBasePvEEventIllumina;

public class DatabaseMagePvEEventIllumina extends DatabaseBasePvEEventIllumina implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventIllumina pyromancer = new DatabaseBasePvEEventIllumina();
    protected DatabaseBasePvEEventIllumina cryomancer = new DatabaseBasePvEEventIllumina();
    protected DatabaseBasePvEEventIllumina aquamancer = new DatabaseBasePvEEventIllumina();

    public DatabaseMagePvEEventIllumina() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventIllumina[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventIllumina getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventIllumina getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventIllumina getAquamancer() {
        return aquamancer;
    }

}
