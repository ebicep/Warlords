package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.PvEEventBoltaroBonanzaStatsWarlordsSpecs;

public class DatabaseMagePvEEventBoltaroBonanza implements PvEEventBoltaroBonanzaStatsWarlordsSpecs {

    protected DatabaseBasePvEEventBoltaroBonanza pyromancer = new DatabaseBasePvEEventBoltaroBonanza();
    protected DatabaseBasePvEEventBoltaroBonanza cryomancer = new DatabaseBasePvEEventBoltaroBonanza();
    protected DatabaseBasePvEEventBoltaroBonanza aquamancer = new DatabaseBasePvEEventBoltaroBonanza();

    public DatabaseMagePvEEventBoltaroBonanza() {
        super();
    }

    @Override
    public DatabaseBasePvEEventBoltaroBonanza[] getSpecs() {
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
