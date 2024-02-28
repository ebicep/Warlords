package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.PvEEventLibraryArchivesStats;

public interface PvEEventLibraryArchivesGrimoiresGraveyardStats extends PvEEventLibraryArchivesStats<DatabaseGamePvEEventGrimoiresGraveyard, DatabaseGamePlayerPvEEventGrimoiresGraveyard> {

    long getFastestGameFinished();

}
