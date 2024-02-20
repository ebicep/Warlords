package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

import java.util.List;

public class DatabasePaladinPvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventSpidersDwelling avenger = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling crusader = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling protector = new DatabaseBasePvEEventSpidersDwelling();

    public DatabasePaladinPvEEventSpidersDwelling() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventSpidersDwelling[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventSpidersDwelling getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventSpidersDwelling getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventSpidersDwelling getProtector() {
        return protector;
    }

}
