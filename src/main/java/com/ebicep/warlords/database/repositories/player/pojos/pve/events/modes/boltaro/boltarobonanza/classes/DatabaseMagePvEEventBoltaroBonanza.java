package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.DatabaseBasePvEEventBoltaroBonanza;

import java.util.List;

public class DatabaseMagePvEEventBoltaroBonanza implements StatsWarlordsSpecs<DatabaseBasePvEEventBoltaroBonanza> {

    protected DatabaseBasePvEEventBoltaroBonanza pyromancer = new DatabaseBasePvEEventBoltaroBonanza();
    protected DatabaseBasePvEEventBoltaroBonanza cryomancer = new DatabaseBasePvEEventBoltaroBonanza();
    protected DatabaseBasePvEEventBoltaroBonanza aquamancer = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseMagePvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventBoltaroBonanza[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventBoltaroBonanza getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventBoltaroBonanza getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventBoltaroBonanza getAquamancer() {
        return aquamancer;
    }

}
