package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.PvEEventBoltaroBonanzaStatsWarlordsSpecs;

public class DatabaseShamanPvEEventBoltaroBonanza implements PvEEventBoltaroBonanzaStatsWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroBonanza thunderlord = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza spiritguard = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza earthwarden = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseShamanPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroBonanza[] getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventBoltaroBonanza getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventBoltaroBonanza getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventBoltaroBonanza getEarthwarden() {
        return earthwarden;
    }

}
