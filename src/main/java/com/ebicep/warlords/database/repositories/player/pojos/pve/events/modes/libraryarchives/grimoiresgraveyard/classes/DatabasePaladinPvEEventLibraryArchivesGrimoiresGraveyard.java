package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard;

import java.util.List;

public class DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard extends DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard avenger = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard crusader = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard protector = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getProtector() {
        return protector;
    }

}
