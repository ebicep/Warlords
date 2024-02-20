package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

public class DatabasePaladinPvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmer avenger = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer crusader = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer protector = new DatabaseBasePvEEventNarmer();

    public DatabasePaladinPvEEventNarmer() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventNarmer getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventNarmer getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventNarmer getProtector() {
        return protector;
    }

}
