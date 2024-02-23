package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.MultiPvEEventLibraryArchivesStats;

public interface MultiPvEEventLibraryArchivesGrimoiresGraveyardStats extends MultiPvEEventLibraryArchivesStats<
        PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsClasses,
        DatabaseGamePvEEventGrimoiresGraveyard,
        DatabaseGamePlayerPvEEventGrimoiresGraveyard,
        PvEEventLibraryArchivesGrimoiresGraveyardStats,
        PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsSpecs>,
        PvEEventLibraryArchivesGrimoiresGraveyardStats {

    @Override
    default long getFastestGameFinished() {
        return getStat(PvEEventLibraryArchivesGrimoiresGraveyardStats::getFastestGameFinished, Math::min, Long.MAX_VALUE);
    }

}
        