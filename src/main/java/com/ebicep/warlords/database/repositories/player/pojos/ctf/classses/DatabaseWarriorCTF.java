package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseWarriorCTF extends DatabaseBaseCTF implements DatabaseWarlordsClass {

    private final DatabaseBaseCTF berserker = new DatabaseBaseCTF();
    private final DatabaseBaseCTF defender = new DatabaseBaseCTF();
    private final DatabaseBaseCTF revenant = new DatabaseBaseCTF();

    public DatabaseWarriorCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
