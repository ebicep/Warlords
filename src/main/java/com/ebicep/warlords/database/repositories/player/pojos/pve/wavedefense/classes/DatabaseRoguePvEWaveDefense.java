package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.WaveDefenseStatsWarlordsSpecs;

public class DatabaseRoguePvEWaveDefense implements WaveDefenseStatsWarlordsSpecs {

    private DatabaseBasePvEWaveDefense assassin = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense vindicator = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense apothecary = new DatabaseBasePvEWaveDefense();

    public DatabaseRoguePvEWaveDefense() {
        super();
    }

    @Override
    public DatabaseBasePvEWaveDefense[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEWaveDefense getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEWaveDefense getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEWaveDefense getApothecary() {
        return apothecary;
    }
}
