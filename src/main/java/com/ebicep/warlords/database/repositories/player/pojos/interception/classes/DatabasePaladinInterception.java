package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;

import com.ebicep.warlords.database.repositories.player.pojos.interception.InterceptionStatsWarlordsSpecs;

public class DatabasePaladinInterception implements InterceptionStatsWarlordsSpecs {

    private DatabaseBaseInterception avenger = new DatabaseBaseInterception();
    private DatabaseBaseInterception crusader = new DatabaseBaseInterception();
    private DatabaseBaseInterception protector = new DatabaseBaseInterception();

    public DatabasePaladinInterception() {
        super();
    }

    @Override
    public DatabaseBaseInterception[] getSpecs() {
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
