package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabaseBaseInterception;

public class DatabasePaladinInterception extends DatabaseBaseInterception implements DatabaseWarlordsClass {

    private DatabaseBaseInterception avenger = new DatabaseBaseInterception();
    private DatabaseBaseInterception crusader = new DatabaseBaseInterception();
    private DatabaseBaseInterception protector = new DatabaseBaseInterception();

    public DatabasePaladinInterception() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseInterception[]{avenger, crusader, protector};
    }

    public DatabaseBaseInterception getAvenger() {
        return avenger;
    }

    public DatabaseBaseInterception getCrusader() {
        return crusader;
    }

    public DatabaseBaseInterception getProtector() {
        return protector;
    }

}
