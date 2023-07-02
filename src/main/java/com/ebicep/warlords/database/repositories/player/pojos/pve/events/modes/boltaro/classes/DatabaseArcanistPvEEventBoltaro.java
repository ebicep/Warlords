
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

public class DatabaseArcanistPvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaro conjurer = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro sentinel = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro cleric = new DatabaseBasePvEEventBoltaro();

    public DatabaseArcanistPvEEventBoltaro() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{conjurer, sentinel, cleric};
    }


    public DatabaseBasePvEEventBoltaro getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaro getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventBoltaro getCleric() {
        return cleric;
    }

}
