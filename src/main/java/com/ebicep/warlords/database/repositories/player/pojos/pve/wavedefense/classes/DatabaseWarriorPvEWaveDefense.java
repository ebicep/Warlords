package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

public class DatabaseWarriorPvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsClass {

    private DatabaseBasePvEWaveDefense berserker = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense defender = new DatabaseBasePvEWaveDefense();
    private DatabaseBasePvEWaveDefense revenant = new DatabaseBasePvEWaveDefense();

    public DatabaseWarriorPvEWaveDefense() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
