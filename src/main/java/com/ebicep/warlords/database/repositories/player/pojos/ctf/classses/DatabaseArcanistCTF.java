package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseArcanistCTF extends DatabaseBaseCTF implements DatabaseWarlordsSpecs {

    private DatabaseBaseCTF conjurer = new DatabaseBaseCTF();
    private DatabaseBaseCTF sentinel = new DatabaseBaseCTF();
    private DatabaseBaseCTF luminary = new DatabaseBaseCTF();

    public DatabaseArcanistCTF() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseCTF[]{conjurer, sentinel, luminary};
    }


    public DatabaseBaseCTF getConjurer() {
        return conjurer;
    }

    public DatabaseBaseCTF getSentinel() {
        return sentinel;
    }

    public DatabaseBaseCTF getLuminary() {
        return luminary;
    }

}
