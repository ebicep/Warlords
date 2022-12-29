package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

public class DatabaseRoguePvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventBoltaroLair assassin = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair vindicator = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair apothecary = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseRoguePvEEventBoltaroLair() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventBoltaroLair getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventBoltaroLair getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventBoltaroLair getApothecary() {
        return apothecary;
    }
}
