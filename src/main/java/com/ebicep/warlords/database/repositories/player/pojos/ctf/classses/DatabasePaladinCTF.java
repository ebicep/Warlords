package com.ebicep.warlords.database.repositories.player.pojos.ctf.classses;

import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabaseBaseCTF;

public class DatabasePaladinCTF implements StatsWarlordsSpecs<DatabaseBaseCTF> {

    private DatabaseBaseCTF avenger = new DatabaseBaseCTF();
    private DatabaseBaseCTF crusader = new DatabaseBaseCTF();
    private DatabaseBaseCTF protector = new DatabaseBaseCTF();

    public DatabasePaladinCTF() {
        super();
    }

    @Override
    public DatabaseBaseCTF[] getSpecs() {
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
