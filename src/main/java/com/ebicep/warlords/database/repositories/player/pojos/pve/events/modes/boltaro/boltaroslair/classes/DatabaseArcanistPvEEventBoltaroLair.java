
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

public class DatabaseArcanistPvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair conjurer = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair sentinel = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair luminary = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseArcanistPvEEventBoltaroLair() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroLair[]{conjurer, sentinel, luminary};
    }

    public DatabaseBasePvEEventBoltaroLair getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaroLair getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventBoltaroLair getLuminary() {
        return luminary;
    }

}
