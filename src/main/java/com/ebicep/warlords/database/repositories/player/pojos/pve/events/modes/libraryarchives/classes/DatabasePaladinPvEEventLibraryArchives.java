package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

import java.util.List;

public class DatabasePaladinPvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchives avenger = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives crusader = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives protector = new DatabaseBasePvEEventLibraryArchives();

    public DatabasePaladinPvEEventLibraryArchives() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
