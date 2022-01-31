package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabasePaladinCTF extends DatabaseBaseCTF implements DatabaseWarlordsClass {

    private DatabaseBaseCTF avenger = new DatabaseBaseCTF();
    private DatabaseBaseCTF crusader = new DatabaseBaseCTF();
    private DatabaseBaseCTF protector = new DatabaseBaseCTF();

    public DatabasePaladinCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseCTF[]{avenger, crusader, protector};
    }

    public DatabaseBaseCTF getAvenger() {
        return avenger;
    }

    public DatabaseBaseCTF getCrusader() {
        return crusader;
    }

    public DatabaseBaseCTF getProtector() {
        return protector;
    }

}
