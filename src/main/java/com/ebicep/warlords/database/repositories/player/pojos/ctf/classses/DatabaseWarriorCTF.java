package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;


import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseWarriorCTF implements StatsWarlordsSpecs<DatabaseBaseCTF> {

    private DatabaseBaseCTF berserker = new DatabaseBaseCTF();
    private DatabaseBaseCTF defender = new DatabaseBaseCTF();
    private DatabaseBaseCTF revenant = new DatabaseBaseCTF();

    public DatabaseWarriorCTF() {
        super();
    }

    @Override
    public DatabaseBaseCTF[] getSpecs() {
        return new DatabaseBaseCTF[]{berserker, defender, revenant};
    }


    public DatabaseBaseCTF getBerserker() {
        return berserker;
    }

    public DatabaseBaseCTF getDefender() {
        return defender;
    }

    public DatabaseBaseCTF getRevenant() {
        return revenant;
    }

}
