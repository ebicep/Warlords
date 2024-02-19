
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.DatabaseBasePvEEventGardenOfHesperidesTartarus;

public class DatabaseArcanistPvEEventGardenOfHesperidesTartarus extends DatabaseBasePvEEventGardenOfHesperidesTartarus implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperidesTartarus conjurer = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus sentinel = new DatabaseBasePvEEventGardenOfHesperidesTartarus();
    private DatabaseBasePvEEventGardenOfHesperidesTartarus luminary = new DatabaseBasePvEEventGardenOfHesperidesTartarus();

    public DatabaseArcanistPvEEventGardenOfHesperidesTartarus() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperidesTartarus[]{conjurer, sentinel, luminary};
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventGardenOfHesperidesTartarus getLuminary() {
        return luminary;
    }

}
