package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public class DatabasePaladinDuel implements StatsWarlordsSpecs<DatabaseBaseDuel> {

    private DatabaseBaseDuel avenger = new DatabaseBaseDuel();
    private DatabaseBaseDuel crusader = new DatabaseBaseDuel();
    private DatabaseBaseDuel protector = new DatabaseBaseDuel();

    public DatabasePaladinDuel() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
