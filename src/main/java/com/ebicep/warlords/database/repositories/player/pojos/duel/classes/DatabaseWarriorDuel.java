package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;


import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabaseBaseDuel;

public class DatabaseWarriorDuel implements StatsWarlordsSpecs<DatabaseBaseDuel> {

    private DatabaseBaseDuel berserker = new DatabaseBaseDuel();
    private DatabaseBaseDuel defender = new DatabaseBaseDuel();
    private DatabaseBaseDuel revenant = new DatabaseBaseDuel();

    public DatabaseWarriorDuel() {
        super();
    }

    @Override
    public DatabaseBaseDuel[] getSpecs() {
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
