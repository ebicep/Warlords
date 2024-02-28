
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair.PvEEventBoltaroLairStatsWarlordsSpecs;

public class DatabaseArcanistPvEEventBoltaroLair implements PvEEventBoltaroLairStatsWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroLair conjurer = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair sentinel = new DatabaseBasePvEEventBoltaroLair();
    private DatabaseBasePvEEventBoltaroLair luminary = new DatabaseBasePvEEventBoltaroLair();

    public DatabaseArcanistPvEEventBoltaroLair() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroLair[] getSpecs() {
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
