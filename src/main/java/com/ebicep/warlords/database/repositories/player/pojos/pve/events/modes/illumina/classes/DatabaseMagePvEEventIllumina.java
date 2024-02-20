package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabaseBasePvEEventIllumina;

import java.util.List;

public class DatabaseMagePvEEventIllumina extends DatabaseBasePvEEventIllumina implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventIllumina pyromancer = new DatabaseBasePvEEventIllumina();
    protected DatabaseBasePvEEventIllumina cryomancer = new DatabaseBasePvEEventIllumina();
    protected DatabaseBasePvEEventIllumina aquamancer = new DatabaseBasePvEEventIllumina();

    public DatabaseMagePvEEventIllumina() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
