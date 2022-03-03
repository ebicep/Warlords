package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabaseBaseDuel;

public class DatabaseRogueDuel extends DatabaseBaseDuel implements DatabaseWarlordsClass {

    private DatabaseBaseDuel assassin = new DatabaseBaseDuel();
    private DatabaseBaseDuel vindicator = new DatabaseBaseDuel();
    private DatabaseBaseDuel apothecary = new DatabaseBaseDuel();

    public DatabaseRogueDuel() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseDuel[]{assassin, vindicator, apothecary};
    }


    public DatabaseBaseDuel getAssassin() {
        return assassin;
    }

    public DatabaseBaseDuel getVindicator() {
        return vindicator;
    }

    public DatabaseBaseDuel getApothecary() {
        return apothecary;
    }
}
