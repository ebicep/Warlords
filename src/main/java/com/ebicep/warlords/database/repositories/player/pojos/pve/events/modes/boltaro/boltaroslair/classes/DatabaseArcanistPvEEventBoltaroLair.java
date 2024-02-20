
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.DatabaseBasePvEEventBoltaroLair;

import java.util.List;

public class DatabaseArcanistPvEEventBoltaroLair extends DatabaseBasePvEEventBoltaroLair implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair conjurer = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair sentinel = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair luminary = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseArcanistPvEEventBoltaroLair() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
