package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabaseBasePvEEventMithra;

import java.util.List;

public class DatabaseRoguePvEEventMithra extends DatabaseBasePvEEventMithra implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventMithra assassin = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra vindicator = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra apothecary = new DatabaseBasePvEEventMithra();

    public DatabaseRoguePvEEventMithra() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventMithra[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventMithra getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventMithra getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventMithra getApothecary() {
        return apothecary;
    }
}
