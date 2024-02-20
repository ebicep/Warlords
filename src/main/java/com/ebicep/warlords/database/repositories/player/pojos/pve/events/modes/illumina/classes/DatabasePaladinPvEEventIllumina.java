package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabaseBasePvEEventIllumina;

import java.util.List;

public class DatabasePaladinPvEEventIllumina extends DatabaseBasePvEEventIllumina implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventIllumina avenger = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina crusader = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina protector = new DatabaseBasePvEEventIllumina();

    public DatabasePaladinPvEEventIllumina() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventIllumina[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventIllumina getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventIllumina getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventIllumina getProtector() {
        return protector;
    }

}
