package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabaseShamanPvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventBoltaro thunderlord = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro spiritguard = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro earthwarden = new DatabaseBasePvEEventBoltaro();

    public DatabaseShamanPvEEventBoltaro() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
