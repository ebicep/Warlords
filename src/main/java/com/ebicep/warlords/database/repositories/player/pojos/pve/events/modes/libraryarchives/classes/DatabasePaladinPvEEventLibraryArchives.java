package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

public class DatabasePaladinPvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchives avenger = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives crusader = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives protector = new DatabaseBasePvEEventLibraryArchives();

    public DatabasePaladinPvEEventLibraryArchives() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventLibraryArchives[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventLibraryArchives getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventLibraryArchives getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventLibraryArchives getProtector() {
        return protector;
    }

}
