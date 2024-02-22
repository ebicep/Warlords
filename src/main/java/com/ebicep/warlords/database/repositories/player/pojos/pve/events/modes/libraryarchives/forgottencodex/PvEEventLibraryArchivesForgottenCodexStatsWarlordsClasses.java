package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.PvEEventLibraryArchivesStatsWarlordsClasses;

public interface PvEEventLibraryArchivesForgottenCodexStatsWarlordsClasses extends PvEEventLibraryArchivesStatsWarlordsClasses<
        DatabaseGamePvEEventForgottenCodex,
        DatabaseGamePlayerPvEEventForgottenCodex,
        PvEEventLibraryArchivesForgottenCodexStats,
        PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs>,
        PvEEventLibraryArchivesForgottenCodexStats {

    @Override
    default long getFastestGameFinished() {
        return getStat(PvEEventLibraryArchivesForgottenCodexStats::getFastestGameFinished, Long::min, Long.MAX_VALUE);
    }
}
