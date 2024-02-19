package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

public class DatabaseMagePvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEWaveDefense pyromancer = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense cryomancer = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense aquamancer = new DatabaseBasePvEWaveDefense();

    public DatabaseMagePvEWaveDefense() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEWaveDefense getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEWaveDefense getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEWaveDefense getAquamancer() {
        return aquamancer;
    }

}
