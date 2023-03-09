package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

public class DatabaseShamanPvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventSpidersDwelling thunderlord = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling spiritguard = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling earthwarden = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseShamanPvEEventSpidersDwelling() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventSpidersDwelling[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventSpidersDwelling getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventSpidersDwelling getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventSpidersDwelling getEarthwarden() {
        return earthwarden;
    }

}
