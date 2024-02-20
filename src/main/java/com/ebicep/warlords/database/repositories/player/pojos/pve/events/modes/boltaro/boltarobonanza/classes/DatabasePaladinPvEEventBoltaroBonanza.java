package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

import java.util.List;

public class DatabasePaladinPvEEventBoltaroBonanza implements StatsWarlordsSpecs<DatabaseBasePvEEventBoltaroBonanza> {

    private DatabaseBasePvEEventBoltaroBonanza avenger = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza crusader = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza protector = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabasePaladinPvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventBoltaroBonanza getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventBoltaroBonanza getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventBoltaroBonanza getProtector() {
        return protector;
    }

}
