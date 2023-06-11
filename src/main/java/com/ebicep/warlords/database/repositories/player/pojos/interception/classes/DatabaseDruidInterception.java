package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabaseBaseInterception;

public class DatabaseDruidInterception extends DatabaseBaseInterception implements DatabaseWarlordsSpecs {

    private DatabaseBaseInterception conjurer = new DatabaseBaseInterception();
    private DatabaseBaseInterception guardian = new DatabaseBaseInterception();
    private DatabaseBaseInterception priest = new DatabaseBaseInterception();

    public DatabaseDruidInterception() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseInterception[]{conjurer, guardian, priest};
    }


    public DatabaseBaseInterception getConjurer() {
        return conjurer;
    }

    public DatabaseBaseInterception getGuardian() {
        return guardian;
    }

    public DatabaseBaseInterception getPriest() {
        return priest;
    }

}
