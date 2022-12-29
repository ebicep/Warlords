package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabaseRoguePvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventBoltaro assassin = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro vindicator = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro apothecary = new DatabaseBasePvEEventBoltaro();

    public DatabaseRoguePvEEventBoltaro() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventBoltaro getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventBoltaro getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventBoltaro getApothecary() {
        return apothecary;
    }
}
