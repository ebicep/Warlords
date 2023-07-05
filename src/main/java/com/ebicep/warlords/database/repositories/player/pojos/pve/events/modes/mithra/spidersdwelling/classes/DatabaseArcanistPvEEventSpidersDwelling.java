package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

public class DatabaseArcanistPvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventSpidersDwelling conjurer = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling sentinel = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling luminary = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseArcanistPvEEventSpidersDwelling() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventSpidersDwelling[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventSpidersDwelling getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventSpidersDwelling getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventSpidersDwelling getLuminary() {
        return luminary;
    }

}
