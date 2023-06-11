
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

public class DatabaseDruidPvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair conjurer = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair guardian = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair priest = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseDruidPvEEventBoltaroLair() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{conjurer, guardian, priest};
    }

    public DatabaseBasePvEEventBoltaroLair getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaroLair getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEventBoltaroLair getPriest() {
        return priest;
    }

}
