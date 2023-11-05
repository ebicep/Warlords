package com.ebicep.warlords.database.repositories.player.pojos.siege.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabaseBaseSiege;

public class DatabaseWarriorSiege extends DatabaseBaseSiege implements DatabaseWarlordsSpecs {

    private DatabaseBaseSiege berserker = new DatabaseBaseSiege();
    private DatabaseBaseSiege defender = new DatabaseBaseSiege();
    private DatabaseBaseSiege revenant = new DatabaseBaseSiege();

    public DatabaseWarriorSiege() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseSiege[]{berserker, defender, revenant};
    }


    public DatabaseBaseSiege getBerserker() {
        return berserker;
    }

    public DatabaseBaseSiege getDefender() {
        return defender;
    }

    public DatabaseBaseSiege getRevenant() {
        return revenant;
    }

}
