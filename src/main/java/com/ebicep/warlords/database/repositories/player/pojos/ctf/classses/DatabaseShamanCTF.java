package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseShamanCTF implements StatsWarlordsSpecs<DatabaseBaseCTF> {

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
