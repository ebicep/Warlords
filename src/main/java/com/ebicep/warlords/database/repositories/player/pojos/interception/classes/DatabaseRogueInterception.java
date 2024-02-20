package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;

import com.ebicep.warlords.database.repositories.player.pojos.interception.InterceptionStatsWarlordsSpecs;

import java.util.List;

public class DatabaseRogueInterception implements InterceptionStatsWarlordsSpecs {

    private DatabaseBaseInterception assassin = new DatabaseBaseInterception();
    private DatabaseBaseInterception vindicator = new DatabaseBaseInterception();
    private DatabaseBaseInterception apothecary = new DatabaseBaseInterception();

    public DatabaseRogueInterception() {
        super();
    }

    @Override
    public List<List<T>> getSpecs() {
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
