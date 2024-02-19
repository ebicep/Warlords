package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabaseBaseInterception;

public class DatabaseRogueInterception implements StatsWarlordsSpecs<DatabaseBaseInterception> {

    private DatabaseBaseInterception assassin = new DatabaseBaseInterception();
    private DatabaseBaseInterception vindicator = new DatabaseBaseInterception();
    private DatabaseBaseInterception apothecary = new DatabaseBaseInterception();

    public DatabaseRogueInterception() {
        super();
    }

    @Override
    public DatabaseBaseInterception[] getSpecs() {
        return new DatabaseBaseInterception[]{assassin, vindicator, apothecary};
    }


    public DatabaseBaseInterception getAssassin() {
        return assassin;
    }

    public DatabaseBaseInterception getVindicator() {
        return vindicator;
    }

    public DatabaseBaseInterception getApothecary() {
        return apothecary;
    }
}
