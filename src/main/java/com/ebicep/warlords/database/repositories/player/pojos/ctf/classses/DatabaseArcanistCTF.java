package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;


import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseArcanistCTF implements StatsWarlordsSpecs<DatabaseBaseCTF> {

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
