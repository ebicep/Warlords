
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

public class DatabaseArcanistPvEEventBoltaroBonanza extends DatabaseBasePvEEventBoltaroBonanza implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroBonanza conjurer = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza sentinel = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza luminary = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseArcanistPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventBoltaroBonanza getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventBoltaroBonanza getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventBoltaroBonanza getLuminary() {
        return luminary;
    }

}
