package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabaseBaseInterception;

public class DatabaseWarriorInterception extends DatabaseBaseInterception implements DatabaseWarlordsClass {

    private DatabaseBaseInterception berserker = new DatabaseBaseInterception();
    private DatabaseBaseInterception defender = new DatabaseBaseInterception();
    private DatabaseBaseInterception revenant = new DatabaseBaseInterception();

    public DatabaseWarriorInterception() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
