package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.DatabaseBasePvEEventIllumina;

public class DatabaseArcanistPvEEventIllumina extends DatabaseBasePvEEventIllumina implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventIllumina conjurer = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina sentinel = new DatabaseBasePvEEventIllumina();
    private DatabaseBasePvEEventIllumina luminary = new DatabaseBasePvEEventIllumina();

    public DatabaseArcanistPvEEventIllumina() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventIllumina[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventIllumina getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventIllumina getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventIllumina getLuminary() {
        return luminary;
    }

}
