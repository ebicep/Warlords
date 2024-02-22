package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs;

public class DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard implements PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard thunderlord = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard spiritguard = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard earthwarden = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[] getSpecs() {
        return new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getEarthwarden() {
        return earthwarden;
    }

}
