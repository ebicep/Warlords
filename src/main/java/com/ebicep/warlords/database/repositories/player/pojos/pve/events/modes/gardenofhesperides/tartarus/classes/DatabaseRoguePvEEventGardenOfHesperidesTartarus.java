package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabaseBasePvEEventGardenOfHesperidesTartarus;

public class DatabaseRoguePvEEventGardenOfHesperidesTartarus extends DatabaseBasePvEEventGardenOfHesperidesTartarus implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesTartarus assassin = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus vindicator = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus apothecary = new DatabaseBasePvEEventGardenOfHesperidesTartarus();

    public DatabaseRoguePvEEventGardenOfHesperidesTartarus() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesTartarus[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventGardenOfHesperidesTartarus getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getApothecary() {
        return apothecary;
    }
}
