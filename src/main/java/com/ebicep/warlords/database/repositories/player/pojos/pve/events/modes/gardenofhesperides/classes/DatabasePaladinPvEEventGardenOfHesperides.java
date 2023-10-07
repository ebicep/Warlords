package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabaseBasePvEEventGardenOfHesperides;

public class DatabasePaladinPvEEventGardenOfHesperides extends DatabaseBasePvEEventGardenOfHesperides implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperides avenger = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides crusader = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides protector = new DatabaseBasePvEEventGardenOfHesperides();

    public DatabasePaladinPvEEventGardenOfHesperides() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperides[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventGardenOfHesperides getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventGardenOfHesperides getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventGardenOfHesperides getProtector() {
        return protector;
    }

}
