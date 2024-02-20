package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabaseBasePvEEventIllumina;

import java.util.List;

public class DatabaseShamanPvEEventIllumina extends DatabaseBasePvEEventIllumina implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventIllumina thunderlord = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina spiritguard = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina earthwarden = new DatabaseBasePvEEventIllumina();

    public DatabaseShamanPvEEventIllumina() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventIllumina[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventIllumina getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventIllumina getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventIllumina getEarthwarden() {
        return earthwarden;
    }

}
