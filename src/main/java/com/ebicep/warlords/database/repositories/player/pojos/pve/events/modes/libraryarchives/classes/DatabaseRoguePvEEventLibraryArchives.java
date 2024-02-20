package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

import java.util.List;

public class DatabaseRoguePvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchives assassin = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives vindicator = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives apothecary = new DatabaseBasePvEEventLibraryArchives();

    public DatabaseRoguePvEEventLibraryArchives() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
