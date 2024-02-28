package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.PvEEventLibraryArchivesStatsWarlordsClasses;

public interface PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsClasses extends PvEEventLibraryArchivesStatsWarlordsClasses<
        DatabaseGamePvEEventGrimoiresGraveyard,
        DatabaseGamePlayerPvEEventGrimoiresGraveyard,
        PvEEventLibraryArchivesGrimoiresGraveyardStats,
        PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs>,
        PvEEventLibraryArchivesGrimoiresGraveyardStats {

    @Override
    default long getFastestGameFinished() {
        return getStat(PvEEventLibraryArchivesGrimoiresGraveyardStats::getFastestGameFinished, Long::min, Long.MAX_VALUE);
    }
}
