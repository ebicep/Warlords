package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;

public class DatabasePaladinCTF extends AbstractDatabaseWarlordsClassCTF {

    private DatabaseSpecializationCTF avenger = new DatabaseSpecializationCTF();
    private DatabaseSpecializationCTF crusader = new DatabaseSpecializationCTF();
    private DatabaseSpecializationCTF protector = new DatabaseSpecializationCTF();

    public DatabasePaladinCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseSpecializationCTF[]{avenger, crusader, protector};
    }

    public DatabaseSpecializationCTF getAvenger() {
        return avenger;
    }

    public DatabaseSpecializationCTF getCrusader() {
        return crusader;
    }

    public DatabaseSpecializationCTF getProtector() {
        return protector;
    }

}
