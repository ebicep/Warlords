package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

import java.util.List;

public class DatabaseShamanPvEEventBoltaroBonanza implements StatsWarlordsSpecs<DatabaseBasePvEEventBoltaroBonanza> {

    private DatabaseBasePvEEventBoltaroBonanza thunderlord = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza spiritguard = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza earthwarden = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseShamanPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
