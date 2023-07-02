package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

public class DatabaseArcanistPvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmer conjurer = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer sentinel = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer cleric = new DatabaseBasePvEEventNarmer();

    public DatabaseArcanistPvEEventNarmer() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{conjurer, sentinel, cleric};
    }


    public DatabaseBasePvEEventNarmer getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventNarmer getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventNarmer getCleric() {
        return cleric;
    }
}
