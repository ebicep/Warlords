package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabaseBaseDuel;

public class DatabasePaladinDuel extends DatabaseBaseDuel implements DatabaseWarlordsClass {

    private DatabaseBaseDuel avenger = new DatabaseBaseDuel();
    private DatabaseBaseDuel crusader = new DatabaseBaseDuel();
    private DatabaseBaseDuel protector = new DatabaseBaseDuel();

    public DatabasePaladinDuel() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseDuel[]{avenger, crusader, protector};
    }

    public DatabaseBaseDuel getAvenger() {
        return avenger;
    }

    public DatabaseBaseDuel getCrusader() {
        return crusader;
    }

    public DatabaseBaseDuel getProtector() {
        return protector;
    }

}
