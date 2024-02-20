package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.WaveDefenseStatsWarlordsSpecs;

public class DatabasePaladinPvEWaveDefense implements WaveDefenseStatsWarlordsSpecs {

    private DatabaseBasePvEWaveDefense avenger = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense crusader = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense protector = new DatabaseBasePvEWaveDefense();

    public DatabasePaladinPvEWaveDefense() {
        super();
    }

    @Override
    public DatabaseBasePvEWaveDefense[] getSpecs() {
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
