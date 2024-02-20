package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabaseBasePvEEventIllumina;

public class DatabaseRoguePvEEventIllumina extends DatabaseBasePvEEventIllumina implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventIllumina assassin = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina vindicator = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina apothecary = new DatabaseBasePvEEventIllumina();

    public DatabaseRoguePvEEventIllumina() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventIllumina[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventIllumina getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventIllumina getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventIllumina getApothecary() {
        return apothecary;
    }
}
