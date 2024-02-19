package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

public class DatabasePaladinPvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEWaveDefense avenger = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense crusader = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense protector = new DatabaseBasePvEWaveDefense();

    public DatabasePaladinPvEWaveDefense() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEWaveDefense getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEWaveDefense getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEWaveDefense getProtector() {
        return protector;
    }

}
