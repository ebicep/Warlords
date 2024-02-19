package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

public class DatabaseArcanistPvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmer conjurer = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer sentinel = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer luminary = new DatabaseBasePvEEventNarmer();

    public DatabaseArcanistPvEEventNarmer() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventNarmer getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventNarmer getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventNarmer getLuminary() {
        return luminary;
    }
}
