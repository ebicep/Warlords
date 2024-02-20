package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;


import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;

public class DatabaseArcanistDuel implements StatsWarlordsSpecs<DatabaseBaseDuel> {

    private DatabaseBaseDuel conjurer = new DatabaseBaseDuel();
    private DatabaseBaseDuel sentinel = new DatabaseBaseDuel();
    private DatabaseBaseDuel luminary = new DatabaseBaseDuel();

    public DatabaseArcanistDuel() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBaseDuel[]{conjurer, sentinel, luminary};
    }


    public DatabaseBaseDuel getConjurer() {
        return conjurer;
    }

    public DatabaseBaseDuel getSentinel() {
        return sentinel;
    }

    public DatabaseBaseDuel getLuminary() {
        return luminary;
    }

}
