package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePlayerPvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.DatabaseGamePvEEventLibraryArchives;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventStatsWarlordsSpecs;

public interface PvEEventLibraryArchivesStatsWarlordsSpecs<
        DatabaseGameT extends DatabaseGamePvEEventLibraryArchives<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEventLibraryArchives,
        T extends PvEEventLibraryArchivesStats<DatabaseGameT, DatabaseGamePlayerT>>
        extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T> {

}
