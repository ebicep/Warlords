
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabaseBasePvEEventBoltaro;

import java.util.List;

public class DatabaseArcanistPvEEventBoltaro extends DatabaseBasePvEEventBoltaro implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaro conjurer = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro sentinel = new DatabaseBasePvEEventBoltaro();
    private DatabaseBasePvEEventBoltaro luminary = new DatabaseBasePvEEventBoltaro();

    public DatabaseArcanistPvEEventBoltaro() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaro[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventBoltaro getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaro getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventBoltaro getLuminary() {
        return luminary;
    }

}
