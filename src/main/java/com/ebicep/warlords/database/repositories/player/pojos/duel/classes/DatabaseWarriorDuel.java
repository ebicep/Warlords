package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabaseBaseDuel;

public class DatabaseWarriorDuel extends DatabaseBaseDuel implements DatabaseWarlordsClass {

    private DatabaseBaseDuel berserker = new DatabaseBaseDuel();
    private DatabaseBaseDuel defender = new DatabaseBaseDuel();
    private DatabaseBaseDuel revenant = new DatabaseBaseDuel();

    public DatabaseWarriorDuel() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseDuel[]{berserker, defender, revenant};
    }


    public DatabaseBaseDuel getBerserker() {
        return berserker;
    }

    public DatabaseBaseDuel getDefender() {
        return defender;
    }

    public DatabaseBaseDuel getRevenant() {
        return revenant;
    }

}
