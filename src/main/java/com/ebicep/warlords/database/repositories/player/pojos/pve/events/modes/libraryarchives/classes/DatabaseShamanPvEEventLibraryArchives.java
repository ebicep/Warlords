package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

public class DatabaseShamanPvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchives thunderlord = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives spiritguard = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives earthwarden = new DatabaseBasePvEEventLibraryArchives();

    public DatabaseShamanPvEEventLibraryArchives() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventLibraryArchives[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventLibraryArchives getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventLibraryArchives getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventLibraryArchives getEarthwarden() {
        return earthwarden;
    }

}
