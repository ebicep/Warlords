
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabaseDruidPvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaro conjurer = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro guardian = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro priest = new DatabaseBasePvEEventBoltaro();

    public DatabaseDruidPvEEventBoltaro() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEEventBoltaro getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaro getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEventBoltaro getPriest() {
        return priest;
    }

}
