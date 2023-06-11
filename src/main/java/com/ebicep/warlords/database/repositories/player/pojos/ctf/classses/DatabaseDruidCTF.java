package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseDruidCTF extends DatabaseBaseCTF implements DatabaseWarlordsSpecs {

    private DatabaseBaseCTF conjurer = new DatabaseBaseCTF();
    private DatabaseBaseCTF guardian = new DatabaseBaseCTF();
    private DatabaseBaseCTF priest = new DatabaseBaseCTF();

    public DatabaseDruidCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseCTF[]{conjurer, guardian, priest};
    }


    public DatabaseBaseCTF getConjurer() {
        return conjurer;
    }

    public DatabaseBaseCTF getGuardian() {
        return guardian;
    }

    public DatabaseBaseCTF getPriest() {
        return priest;
    }

}
