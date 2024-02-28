package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.player.pojos.duel.DuelStatsWarlordsSpecs;

public class DatabaseShamanDuel implements DuelStatsWarlordsSpecs {

    private DatabaseBaseDuel thunderlord = new DatabaseBaseDuel();
    private DatabaseBaseDuel spiritguard = new DatabaseBaseDuel();
    private DatabaseBaseDuel earthwarden = new DatabaseBaseDuel();

    public DatabaseShamanDuel() {
        super();
    }

    @Override
    public DatabaseBaseDuel[] getSpecs() {
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
