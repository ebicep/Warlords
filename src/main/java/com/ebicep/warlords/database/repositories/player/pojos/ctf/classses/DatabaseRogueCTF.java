package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseRogueCTF extends DatabaseBaseCTF implements DatabaseWarlordsClass {

    private DatabaseBaseCTF assassin = new DatabaseBaseCTF();
    private DatabaseBaseCTF vindicator = new DatabaseBaseCTF();
    private DatabaseBaseCTF apothecary = new DatabaseBaseCTF();

    public DatabaseRogueCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseCTF[]{assassin, vindicator, apothecary};
    }


    public DatabaseBaseCTF getAssassin() {
        return assassin;
    }

    public DatabaseBaseCTF getVindicator() {
        return vindicator;
    }

    public DatabaseBaseCTF getApothecary() {
        return apothecary;
    }
}
