package com.ebicep.warlords.database.repositories.player.pojos.interception.classes;

import com.ebicep.warlords.database.repositories.player.pojos.interception.InterceptionStatsWarlordsSpecs;

public class DatabaseShamanInterception implements InterceptionStatsWarlordsSpecs {

    private DatabaseBaseInterception thunderlord = new DatabaseBaseInterception();
    private DatabaseBaseInterception spiritguard = new DatabaseBaseInterception();
    private DatabaseBaseInterception earthwarden = new DatabaseBaseInterception();

    public DatabaseShamanInterception() {
        super();
    }

    @Override
    public DatabaseBaseInterception[] getSpecs() {
        return new DatabaseBaseInterception[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBaseInterception getThunderlord() {
        return thunderlord;
    }

    public DatabaseBaseInterception getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBaseInterception getEarthwarden() {
        return earthwarden;
    }

}
