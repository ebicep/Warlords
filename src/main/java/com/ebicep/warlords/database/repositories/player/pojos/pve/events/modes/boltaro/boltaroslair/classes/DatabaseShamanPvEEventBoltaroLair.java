package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

public class DatabaseShamanPvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair thunderlord = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair spiritguard = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair earthwarden = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseShamanPvEEventBoltaroLair() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventBoltaroLair getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventBoltaroLair getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventBoltaroLair getEarthwarden() {
        return earthwarden;
    }

}
