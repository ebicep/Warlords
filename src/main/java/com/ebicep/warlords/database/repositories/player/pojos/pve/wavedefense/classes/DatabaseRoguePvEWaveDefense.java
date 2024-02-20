package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

import java.util.List;

public class DatabaseRoguePvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEWaveDefense assassin = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense vindicator = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense apothecary = new DatabaseBasePvEWaveDefense();

    public DatabaseRoguePvEWaveDefense() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
