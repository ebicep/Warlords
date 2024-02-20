
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

import java.util.List;

public class DatabaseArcanistPvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchives conjurer = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives sentinel = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives luminary = new DatabaseBasePvEEventLibraryArchives();

    public DatabaseArcanistPvEEventLibraryArchives() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventLibraryArchives[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventLibraryArchives getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventLibraryArchives getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventLibraryArchives getLuminary() {
        return luminary;
    }

}
