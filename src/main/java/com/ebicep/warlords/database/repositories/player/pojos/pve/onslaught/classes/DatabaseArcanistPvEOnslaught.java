
package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

public class DatabaseArcanistPvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEOnslaught conjurer = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught sentinel = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught cleric = new DatabaseBasePvEOnslaught();

    public DatabaseArcanistPvEOnslaught() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{conjurer, sentinel, cleric};
    }


    public DatabaseBasePvEOnslaught getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEOnslaught getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEOnslaught getCleric() {
        return cleric;
    }
}
