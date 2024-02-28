package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard.classes.*;
import com.ebicep.warlords.game.GameMode;
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

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventGrimoiresGraveyard databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventGrimoiresGraveyard gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
