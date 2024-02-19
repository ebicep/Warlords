package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

public class DatabaseShamanPvEEventBoltaroBonanza extends DatabaseBasePvEEventBoltaroBonanza implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventBoltaroBonanza thunderlord = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza spiritguard = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza earthwarden = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseShamanPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
