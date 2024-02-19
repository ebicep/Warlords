package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard;

public class DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard extends DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard thunderlord = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard spiritguard = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard earthwarden = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
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
