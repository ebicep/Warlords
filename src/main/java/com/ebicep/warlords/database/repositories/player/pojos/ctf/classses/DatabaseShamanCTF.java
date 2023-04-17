package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseShamanCTF extends DatabaseBaseCTF implements DatabaseWarlordsSpecs {

    private DatabaseBaseCTF thunderlord = new DatabaseBaseCTF();
    private DatabaseBaseCTF spiritguard = new DatabaseBaseCTF();
    private DatabaseBaseCTF earthwarden = new DatabaseBaseCTF();

    public DatabaseShamanCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
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
