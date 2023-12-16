package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

public class DatabaseRoguePvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchives assassin = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives vindicator = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives apothecary = new DatabaseBasePvEEventLibraryArchives();

    public DatabaseRoguePvEEventLibraryArchives() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventLibraryArchives[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventLibraryArchives getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventLibraryArchives getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventLibraryArchives getApothecary() {
        return apothecary;
    }
}
