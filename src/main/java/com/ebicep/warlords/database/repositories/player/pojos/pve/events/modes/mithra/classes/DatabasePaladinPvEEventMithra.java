package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.DatabaseBasePvEEventMithra;

public class DatabasePaladinPvEEventMithra extends DatabaseBasePvEEventMithra implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventMithra avenger = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra crusader = new DatabaseBasePvEEventMithra();
    private DatabaseBasePvEEventMithra protector = new DatabaseBasePvEEventMithra();

    public DatabasePaladinPvEEventMithra() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventMithra[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventMithra getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventMithra getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventMithra getProtector() {
        return protector;
    }

}
