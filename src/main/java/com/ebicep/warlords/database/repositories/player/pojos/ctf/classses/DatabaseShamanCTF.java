package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.ctf.CTFStatsWarlordsSpecs;

public class DatabaseShamanCTF implements CTFStatsWarlordsSpecs {

    private DatabaseBaseCTF thunderlord = new DatabaseBaseCTF();
    private DatabaseBaseCTF spiritguard = new DatabaseBaseCTF();
    private DatabaseBaseCTF earthwarden = new DatabaseBaseCTF();

    public DatabaseShamanCTF() {
        super();
    }

    @Override
    public DatabaseBaseCTF[] getSpecs() {
        return new DatabaseBaseCTF[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBaseCTF getThunderlord() {
        return thunderlord;
    }

    public DatabaseBaseCTF getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBaseCTF getEarthwarden() {
        return earthwarden;
    }

}
