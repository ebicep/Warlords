package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabaseShamanCTF extends AbstractDatabaseWarlordsClassCTF {

    private DatabaseSpecializationCTF thunderlord = new DatabaseSpecializationCTF();
    private DatabaseSpecializationCTF spiritguard = new DatabaseSpecializationCTF();
    private DatabaseSpecializationCTF earthwarden = new DatabaseSpecializationCTF();

    public DatabaseShamanCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseSpecializationCTF[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseSpecializationCTF getThunderlord() {
        return thunderlord;
    }

    public DatabaseSpecializationCTF getSpiritguard() {
        return spiritguard;
    }

    public DatabaseSpecializationCTF getEarthwarden() {
        return earthwarden;
    }

}
