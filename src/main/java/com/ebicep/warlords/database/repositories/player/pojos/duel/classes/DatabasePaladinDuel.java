package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.duel.DuelStatsWarlordsSpecs;

public class DatabasePaladinDuel implements DuelStatsWarlordsSpecs {

    private DatabaseBaseDuel avenger = new DatabaseBaseDuel();
    private DatabaseBaseDuel crusader = new DatabaseBaseDuel();
    private DatabaseBaseDuel protector = new DatabaseBaseDuel();

    public DatabasePaladinDuel() {
        super();
    }

    @Override
    public DatabaseBaseDuel[] getSpecs() {
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
