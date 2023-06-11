
package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabaseBasePvEOnslaught;

public class DatabaseDruidPvEOnslaught extends DatabaseBasePvEOnslaught implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEOnslaught conjurer = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught guardian = new DatabaseBasePvEOnslaught();
    private DatabaseBasePvEOnslaught priest = new DatabaseBasePvEOnslaught();

    public DatabaseDruidPvEOnslaught() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEOnslaught[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEOnslaught getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEOnslaught getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEOnslaught getPriest() {
        return priest;
    }
}
