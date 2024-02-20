package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.DatabaseBasePvEEventLibraryForgottenCodex;

public class DatabaseRoguePvEEventLibraryForgottenCodex extends DatabaseBasePvEEventLibraryForgottenCodex implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryForgottenCodex assassin = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex vindicator = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex apothecary = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabaseRoguePvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventLibraryForgottenCodex getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getApothecary() {
        return apothecary;
    }
}
