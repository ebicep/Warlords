package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard;

import java.util.List;

public class DatabaseMagePvEEventLibraryArchivesGrimoiresGraveyard extends DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard pyromancer = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    protected DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard cryomancer = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    protected DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard aquamancer = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabaseMagePvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getAquamancer() {
        return aquamancer;
    }

}
