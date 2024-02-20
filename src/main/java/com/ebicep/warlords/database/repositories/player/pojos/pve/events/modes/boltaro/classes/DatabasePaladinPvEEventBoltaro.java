package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabasePaladinPvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaro avenger = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro crusader = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro protector = new DatabaseBasePvEEventBoltaro();

    public DatabasePaladinPvEEventBoltaro() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventBoltaro getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventBoltaro getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventBoltaro getProtector() {
        return protector;
    }

}
