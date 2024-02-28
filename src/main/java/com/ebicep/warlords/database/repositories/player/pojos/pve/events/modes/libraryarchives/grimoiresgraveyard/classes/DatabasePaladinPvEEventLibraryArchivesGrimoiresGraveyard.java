package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs;

public class DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard implements PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard avenger = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard crusader = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard protector = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[] getSpecs() {
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
