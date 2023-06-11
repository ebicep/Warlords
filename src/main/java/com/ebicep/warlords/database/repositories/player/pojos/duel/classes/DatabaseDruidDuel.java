package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabaseBaseDuel;

public class DatabaseDruidDuel extends DatabaseBaseDuel implements DatabaseWarlordsSpecs {

    private DatabaseBaseDuel conjurer = new DatabaseBaseDuel();
    private DatabaseBaseDuel guardian = new DatabaseBaseDuel();
    private DatabaseBaseDuel priest = new DatabaseBaseDuel();

    public DatabaseDruidDuel() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseDuel[]{conjurer, guardian, priest};
    }


    public DatabaseBaseDuel getConjurer() {
        return conjurer;
    }

    public DatabaseBaseDuel getGuardian() {
        return guardian;
    }

    public DatabaseBaseDuel getPriest() {
        return priest;
    }

}
