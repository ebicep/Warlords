package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabaseBaseDuel;

public class DatabaseShamanDuel extends DatabaseBaseDuel implements DatabaseWarlordsClass {

    private DatabaseBaseDuel thunderlord = new DatabaseBaseDuel();
    private DatabaseBaseDuel spiritguard = new DatabaseBaseDuel();
    private DatabaseBaseDuel earthwarden = new DatabaseBaseDuel();

    public DatabaseShamanDuel() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseDuel[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBaseDuel getThunderlord() {
        return thunderlord;
    }

    public DatabaseBaseDuel getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBaseDuel getEarthwarden() {
        return earthwarden;
    }

}
