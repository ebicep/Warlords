package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs;

public class DatabaseRoguePvEEventLibraryArchivesGrimoiresGraveyard implements PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard assassin = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard vindicator = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard apothecary = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabaseRoguePvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[] getSpecs() {
        return new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getApothecary() {
        return apothecary;
    }
}
