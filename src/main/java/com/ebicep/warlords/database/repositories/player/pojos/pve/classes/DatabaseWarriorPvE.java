package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;

public class DatabaseWarriorPvE extends DatabaseBasePvE implements DatabaseWarlordsSpecs {

    private DatabaseBasePvE berserker = new DatabaseBasePvE();
    private DatabaseBasePvE defender = new DatabaseBasePvE();
    private DatabaseBasePvE revenant = new DatabaseBasePvE();

    public DatabaseWarriorPvE() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvE[]{berserker, defender, revenant};
    }


    public DatabaseBasePvE getBerserker() {
        return berserker;
    }

    public DatabaseBasePvE getDefender() {
        return defender;
    }

    public DatabaseBasePvE getRevenant() {
        return revenant;
    }

}
