package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

import java.util.List;

public class DatabaseRoguePvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair assassin = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair vindicator = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair apothecary = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseRoguePvEEventBoltaroLair() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
