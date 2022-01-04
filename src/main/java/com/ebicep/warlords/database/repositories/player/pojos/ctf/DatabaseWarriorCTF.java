package com.ebicep.warlords.database.repositories.player.pojos.ctf;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabaseWarriorCTF extends AbstractDatabaseWarlordsClassCTF {

    private DatabaseSpecializationCTF berserker = new DatabaseSpecializationCTF();
    private DatabaseSpecializationCTF defender = new DatabaseSpecializationCTF();
    private DatabaseSpecializationCTF revenant = new DatabaseSpecializationCTF();

    public DatabaseWarriorCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseSpecializationCTF[]{berserker, defender, revenant};
    }


    public DatabaseSpecializationCTF getBerserker() {
        return berserker;
    }

    public DatabaseSpecializationCTF getDefender() {
        return defender;
    }

    public DatabaseSpecializationCTF getRevenant() {
        return revenant;
    }

}
