package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabaseBaseInterception;

public class DatabaseArcanistInterception extends DatabaseBaseInterception implements DatabaseWarlordsSpecs {

    private DatabaseBaseInterception conjurer = new DatabaseBaseInterception();
    private DatabaseBaseInterception sentinel = new DatabaseBaseInterception();
    private DatabaseBaseInterception cleric = new DatabaseBaseInterception();

    public DatabaseArcanistInterception() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseInterception[]{conjurer, sentinel, cleric};
    }


    public DatabaseBaseInterception getConjurer() {
        return conjurer;
    }

    public DatabaseBaseInterception getSentinel() {
        return sentinel;
    }

    public DatabaseBaseInterception getCleric() {
        return cleric;
    }

}
