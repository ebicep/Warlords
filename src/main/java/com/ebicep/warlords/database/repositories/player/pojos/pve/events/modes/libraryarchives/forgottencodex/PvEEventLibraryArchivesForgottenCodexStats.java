package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.PvEEventLibraryArchivesStats;

public interface PvEEventLibraryArchivesForgottenCodexStats extends PvEEventLibraryArchivesStats<DatabaseGamePvEEventForgottenCodex, DatabaseGamePlayerPvEEventForgottenCodex> {

    long getFastestGameFinished();

}
