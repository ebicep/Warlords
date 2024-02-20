
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.DatabaseBasePvEEventGardenOfHesperides;

import java.util.List;

public class DatabaseArcanistPvEEventGardenOfHesperides extends DatabaseBasePvEEventGardenOfHesperides implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventGardenOfHesperides conjurer = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides sentinel = new DatabaseBasePvEEventGardenOfHesperides();
    private DatabaseBasePvEEventGardenOfHesperides luminary = new DatabaseBasePvEEventGardenOfHesperides();

    public DatabaseArcanistPvEEventGardenOfHesperides() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventGardenOfHesperides[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventGardenOfHesperides getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventGardenOfHesperides getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventGardenOfHesperides getLuminary() {
        return luminary;
    }

}
