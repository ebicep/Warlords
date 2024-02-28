package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePlayerPvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStats;

public interface PvEEventLibraryArchivesStats<DatabaseGameT extends DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventLibraryArchives>
        extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

}
