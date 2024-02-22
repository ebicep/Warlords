package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePlayerPvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsClasses;

public interface PvEEventLibraryArchivesStatsWarlordsClasses<DatabaseGameT extends DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventLibraryArchives,
        T extends PvEEventLibraryArchivesStats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends PvEEventLibraryArchivesStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>>
        extends PvEEventStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, T, R> {

}
