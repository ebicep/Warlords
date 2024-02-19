package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabaseRogueCTF implements StatsWarlordsSpecs<DatabaseBaseCTF> {

    private DatabaseBaseCTF assassin = new DatabaseBaseCTF();
    private DatabaseBaseCTF vindicator = new DatabaseBaseCTF();
    private DatabaseBaseCTF apothecary = new DatabaseBaseCTF();

    public DatabaseRogueCTF() {
        super();
    }

    @Override
    public DatabaseBaseCTF[] getSpecs() {
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
