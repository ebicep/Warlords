package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.DatabaseBasePvEEventGardenOfHesperidesAcropolis;

public class DatabasePaladinPvEEventGardenOfHesperidesAcropolis extends DatabaseBasePvEEventGardenOfHesperidesAcropolis implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesAcropolis avenger = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis crusader = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();
    private DatabaseBasePvEEventGardenOfHesperidesAcropolis protector = new DatabaseBasePvEEventGardenOfHesperidesAcropolis();

    public DatabasePaladinPvEEventGardenOfHesperidesAcropolis() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesAcropolis[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventGardenOfHesperidesAcropolis getProtector() {
        return protector;
    }

}
