
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;


import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

import java.util.List;

public class DatabaseArcanistPvEEventBoltaroBonanza implements StatsWarlordsSpecs<DatabaseBasePvEEventBoltaroBonanza> {

    private DatabaseBasePvEEventBoltaroBonanza conjurer = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza sentinel = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza luminary = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseArcanistPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
