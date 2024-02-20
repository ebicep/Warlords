package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.DatabaseBasePvEEventLibraryForgottenCodex;

import java.util.List;

public class DatabaseRoguePvEEventLibraryForgottenCodex extends DatabaseBasePvEEventLibraryForgottenCodex implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryForgottenCodex assassin = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex vindicator = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex apothecary = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabaseRoguePvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public List<List> getSpecs() {
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
