package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs;

public class DatabasePaladinPvEEventLibraryForgottenCodex implements PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs {

    private DatabaseBasePvEEventLibraryForgottenCodex avenger = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex crusader = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex protector = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabasePaladinPvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public DatabaseBasePvEEventLibraryForgottenCodex[] getSpecs() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{avenger, crusader, protector};
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getAvenger() {
        return avenger;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getCrusader() {
        return crusader;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getProtector() {
        return protector;
    }

}
