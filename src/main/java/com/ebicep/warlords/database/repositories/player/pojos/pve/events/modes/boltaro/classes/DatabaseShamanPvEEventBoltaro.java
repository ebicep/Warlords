package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabaseShamanPvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaro thunderlord = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro spiritguard = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro earthwarden = new DatabaseBasePvEEventBoltaro();

    public DatabaseShamanPvEEventBoltaro() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventBoltaro getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventBoltaro getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventBoltaro getEarthwarden() {
        return earthwarden;
    }

}
