package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

import java.util.List;

public class DatabaseRoguePvEEventBoltaroBonanza implements StatsWarlordsSpecs<DatabaseBasePvEEventBoltaroBonanza> {

    private DatabaseBasePvEEventBoltaroBonanza assassin = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza vindicator = new DatabaseBasePvEEventBoltaroBonanza();
    private DatabaseBasePvEEventBoltaroBonanza apothecary = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseRoguePvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventBoltaroBonanza getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventBoltaroBonanza getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventBoltaroBonanza getApothecary() {
        return apothecary;
    }
}
