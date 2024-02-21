package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.PvEEventBoltaroLairStatsWarlordsSpecs;

public class DatabaseShamanPvEEventBoltaroLair implements PvEEventBoltaroLairStatsWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair thunderlord = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair spiritguard = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair earthwarden = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseShamanPvEEventBoltaroLair() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroLair[] getSpecs() {
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
