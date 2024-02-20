package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.WaveDefenseStatsWarlordsSpecs;

public class DatabaseShamanPvEWaveDefense implements WaveDefenseStatsWarlordsSpecs {

    private DatabaseBasePvEWaveDefense thunderlord = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense spiritguard = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense earthwarden = new DatabaseBasePvEWaveDefense();

    public DatabaseShamanPvEWaveDefense() {
        super();
    }

    @Override
    public DatabaseBasePvEWaveDefense[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEWaveDefense getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEWaveDefense getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEWaveDefense getEarthwarden() {
        return earthwarden;
    }

}
