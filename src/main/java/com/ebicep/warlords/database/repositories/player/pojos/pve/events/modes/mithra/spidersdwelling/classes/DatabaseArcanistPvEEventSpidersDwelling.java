package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

import java.util.List;

public class DatabaseArcanistPvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventSpidersDwelling conjurer = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling sentinel = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling luminary = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseArcanistPvEEventSpidersDwelling() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
