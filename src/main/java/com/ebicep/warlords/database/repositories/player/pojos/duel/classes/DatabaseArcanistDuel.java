package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabaseBaseDuel;

public class DatabaseArcanistDuel extends DatabaseBaseDuel implements DatabaseWarlordsSpecs {

    private DatabaseBaseDuel conjurer = new DatabaseBaseDuel();
    private DatabaseBaseDuel sentinel = new DatabaseBaseDuel();
    private DatabaseBaseDuel cleric = new DatabaseBaseDuel();

    public DatabaseArcanistDuel() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseDuel[]{conjurer, sentinel, cleric};
    }


    public DatabaseBaseDuel getConjurer() {
        return conjurer;
    }

    public DatabaseBaseDuel getSentinel() {
        return sentinel;
    }

    public DatabaseBaseDuel getCleric() {
        return cleric;
    }

}
