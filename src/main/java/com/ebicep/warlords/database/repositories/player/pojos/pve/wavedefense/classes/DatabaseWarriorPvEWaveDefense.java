package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

public class DatabaseWarriorPvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEWaveDefense berserker = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense defender = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense revenant = new DatabaseBasePvEWaveDefense();

    public DatabaseWarriorPvEWaveDefense() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEWaveDefense getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEWaveDefense getDefender() {
        return defender;
    }

    public DatabaseBasePvEWaveDefense getRevenant() {
        return revenant;
    }

}
