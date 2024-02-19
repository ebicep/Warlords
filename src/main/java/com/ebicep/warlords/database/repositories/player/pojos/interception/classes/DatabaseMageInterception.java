package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabaseBaseInterception;

public class DatabaseMageInterception implements StatsWarlordsSpecs<DatabaseBaseInterception> {

    protected DatabaseBaseInterception pyromancer = new DatabaseBaseInterception();
    protected DatabaseBaseInterception cryomancer = new DatabaseBaseInterception();
    protected DatabaseBaseInterception aquamancer = new DatabaseBaseInterception();

    public DatabaseMageInterception() {
        super();
    }

    @Override
    public DatabaseBaseInterception[] getSpecs() {
        return new DatabaseBaseInterception[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBaseInterception getPyromancer() {
        return pyromancer;
    }

    public DatabaseBaseInterception getCryomancer() {
        return cryomancer;
    }

    public DatabaseBaseInterception getAquamancer() {
        return aquamancer;
    }

}
