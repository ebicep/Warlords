
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

public class DatabaseArcanistPvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair conjurer = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair sentinel = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair cleric = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseArcanistPvEEventBoltaroLair() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{conjurer, sentinel, cleric};
    }

    public DatabaseBasePvEEventBoltaroLair getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaroLair getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventBoltaroLair getCleric() {
        return cleric;
    }

}
