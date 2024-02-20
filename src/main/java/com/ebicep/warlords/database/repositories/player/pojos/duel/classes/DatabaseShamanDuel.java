package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public class DatabaseShamanDuel implements StatsWarlordsSpecs<DatabaseBaseDuel> {

    private DatabaseBaseDuel thunderlord = new DatabaseBaseDuel();
    private DatabaseBaseDuel spiritguard = new DatabaseBaseDuel();
    private DatabaseBaseDuel earthwarden = new DatabaseBaseDuel();

    public DatabaseShamanDuel() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
