package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

import java.util.List;

public class DatabasePaladinPvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair avenger = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair crusader = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair protector = new DatabaseBasePvEEventBoltaroLair();

    public DatabasePaladinPvEEventBoltaroLair() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventBoltaroLair getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventBoltaroLair getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventBoltaroLair getProtector() {
        return protector;
    }

}
