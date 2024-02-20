package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;


import com.ebicep.warlords.database.repositories.player.pojos.ctf.CTFStatsWarlordsSpecs;

import java.util.List;

public class DatabaseWarriorCTF implements CTFStatsWarlordsSpecs {

    private DatabaseBaseCTF berserker = new DatabaseBaseCTF();
    private DatabaseBaseCTF defender = new DatabaseBaseCTF();
    private DatabaseBaseCTF revenant = new DatabaseBaseCTF();

    public DatabaseWarriorCTF() {
        super();
    }

    @Override
    public List<List<DatabaseBaseCTF>> getSpecs() {
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
