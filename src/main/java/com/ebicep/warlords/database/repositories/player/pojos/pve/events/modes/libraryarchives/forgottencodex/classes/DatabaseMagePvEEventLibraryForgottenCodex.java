package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.DatabaseBasePvEEventLibraryForgottenCodex;

public class DatabaseMagePvEEventLibraryForgottenCodex extends DatabaseBasePvEEventLibraryForgottenCodex implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEEventLibraryForgottenCodex pyromancer = new DatabaseBasePvEEventLibraryForgottenCodex();
    protected DatabaseBasePvEEventLibraryForgottenCodex cryomancer = new DatabaseBasePvEEventLibraryForgottenCodex();
    protected DatabaseBasePvEEventLibraryForgottenCodex aquamancer = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabaseMagePvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{pyromancer, cryomancer, aquamancer};
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getPyromancer() {
        return pyromancer;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getCryomancer() {
        return cryomancer;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getAquamancer() {
        return aquamancer;
    }

}
