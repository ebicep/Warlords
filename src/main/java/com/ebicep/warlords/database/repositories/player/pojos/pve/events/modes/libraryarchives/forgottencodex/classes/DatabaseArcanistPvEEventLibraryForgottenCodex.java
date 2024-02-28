
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs;

public class DatabaseArcanistPvEEventLibraryForgottenCodex implements PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs {

    private DatabaseBasePvEEventLibraryForgottenCodex conjurer = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex sentinel = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex luminary = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabaseArcanistPvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public DatabaseBasePvEEventLibraryForgottenCodex[] getSpecs() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{conjurer, sentinel, luminary};
    }


    public DatabaseBasePvEEventLibraryForgottenCodex getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getLuminary() {
        return luminary;
    }

}
