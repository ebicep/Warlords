package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard implements PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard berserker = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard defender = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard revenant = new DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard();

    public DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard() {
        super();
    }

    @Override
    public DatabaseBasePvEEventLibraryArchivesGrimoiresGraveyard[] getSpecs() {
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
