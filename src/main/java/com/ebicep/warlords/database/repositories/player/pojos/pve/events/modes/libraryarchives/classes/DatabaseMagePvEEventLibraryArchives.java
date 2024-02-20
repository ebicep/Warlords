package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

import java.util.List;

public class DatabaseMagePvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventLibraryArchives pyromancer = new DatabaseBasePvEEventLibraryArchives();
    protected DatabaseBasePvEEventLibraryArchives cryomancer = new DatabaseBasePvEEventLibraryArchives();
    protected DatabaseBasePvEEventLibraryArchives aquamancer = new DatabaseBasePvEEventLibraryArchives();

    public DatabaseMagePvEEventLibraryArchives() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventLibraryArchives[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventLibraryArchives getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventLibraryArchives getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventLibraryArchives getAquamancer() {
        return aquamancer;
    }

}
