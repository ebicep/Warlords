
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.PvEEventBoltaroBonanzaStatsWarlordsSpecs;

public class DatabaseArcanistPvEEventBoltaroBonanza implements PvEEventBoltaroBonanzaStatsWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroBonanza conjurer = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza sentinel = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza luminary = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseArcanistPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroBonanza[] getSpecs() {
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
