package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes.*;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats implements PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsClasses {

    private DatabaseMagePvEEventLibraryArchivesGrimoiresGraveyard mage = new DatabaseMagePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard warrior = new DatabaseWarriorPvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard paladin = new DatabasePaladinPvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard shaman = new DatabaseShamanPvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseRoguePvEEventLibraryArchivesGrimoiresGraveyard rogue = new DatabaseRoguePvEEventLibraryArchivesGrimoiresGraveyard();
    private DatabaseArcanistPvEEventLibraryArchivesGrimoiresGraveyard arcanist = new DatabaseArcanistPvEEventLibraryArchivesGrimoiresGraveyard();

    @Override
    public PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }

}
