package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabaseBasePvEEventMithra;

import java.util.List;

public class DatabaseShamanPvEEventMithra extends DatabaseBasePvEEventMithra implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventMithra thunderlord = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra spiritguard = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra earthwarden = new DatabaseBasePvEEventMithra();

    public DatabaseShamanPvEEventMithra() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventMithra[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventMithra getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventMithra getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventMithra getEarthwarden() {
        return earthwarden;
    }

}
