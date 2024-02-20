package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;


import com.ebicep.warlords.database.repositories.player.pojos.interception.InterceptionStatsWarlordsSpecs;

import java.util.List;

public class DatabaseWarriorInterception implements InterceptionStatsWarlordsSpecs {

    private DatabaseBaseInterception berserker = new DatabaseBaseInterception();
    private DatabaseBaseInterception defender = new DatabaseBaseInterception();
    private DatabaseBaseInterception revenant = new DatabaseBaseInterception();

    public DatabaseWarriorInterception() {
        super();
    }

    @Override
    public List<List<T>> getSpecs() {
        return new DatabaseBaseInterception[]{berserker, defender, revenant};
    }


    public DatabaseBaseInterception getBerserker() {
        return berserker;
    }

    public DatabaseBaseInterception getDefender() {
        return defender;
    }

    public DatabaseBaseInterception getRevenant() {
        return revenant;
    }

}
