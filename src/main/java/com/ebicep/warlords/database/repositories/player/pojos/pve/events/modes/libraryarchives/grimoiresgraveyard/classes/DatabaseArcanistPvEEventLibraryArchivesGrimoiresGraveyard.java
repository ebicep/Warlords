
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard;

import java.util.List;

public class DatabaseArcanistPvEEventLibraryArchivesGrimoiresGraveyard extends DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard conjurer = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard sentinel = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard luminary = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabaseArcanistPvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[]{conjurer, sentinel, luminary};
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getLuminary() {
        return luminary;
    }

}
