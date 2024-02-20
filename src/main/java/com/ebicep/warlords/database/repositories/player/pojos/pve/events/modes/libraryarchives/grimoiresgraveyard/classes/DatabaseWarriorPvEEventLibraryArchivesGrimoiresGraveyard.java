package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard;

public class DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard extends DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard berserker = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard defender = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard revenant = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[]{berserker, defender, revenant};
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard getRevenant() {
        return revenant;
    }

}
