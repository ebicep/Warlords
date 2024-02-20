package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabaseBasePvEEventMithra;

import java.util.List;

public class DatabaseArcanistPvEEventMithra extends DatabaseBasePvEEventMithra implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventMithra conjurer = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra sentinel = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra luminary = new DatabaseBasePvEEventMithra();

    public DatabaseArcanistPvEEventMithra() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventMithra[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventMithra getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventMithra getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventMithra getLuminary() {
        return luminary;
    }

}
