package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabaseBasePvEEventGardenOfHesperides;

public class DatabaseRoguePvEEventGardenOfHesperides extends DatabaseBasePvEEventGardenOfHesperides implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperides assassin = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides vindicator = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides apothecary = new DatabaseBasePvEEventGardenOfHesperides();

    public DatabaseRoguePvEEventGardenOfHesperides() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperides[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventGardenOfHesperides getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventGardenOfHesperides getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventGardenOfHesperides getApothecary() {
        return apothecary;
    }
}
