package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;


import com.ebicep.warlords.database.repositories.player.pojos.ctf.CTFStatsWarlordsSpecs;

public class DatabaseArcanistCTF implements CTFStatsWarlordsSpecs {

    private DatabaseBaseCTF conjurer = new DatabaseBaseCTF();
    private DatabaseBaseCTF sentinel = new DatabaseBaseCTF();
    private DatabaseBaseCTF luminary = new DatabaseBaseCTF();

    public DatabaseArcanistCTF() {
        super();
    }

    @Override
    public DatabaseBaseCTF[] getSpecs() {
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
