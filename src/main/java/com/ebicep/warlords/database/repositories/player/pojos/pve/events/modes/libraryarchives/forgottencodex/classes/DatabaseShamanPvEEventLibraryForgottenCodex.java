package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.DatabaseBasePvEEventLibraryForgottenCodex;

import java.util.List;

public class DatabaseShamanPvEEventLibraryForgottenCodex extends DatabaseBasePvEEventLibraryForgottenCodex implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryForgottenCodex thunderlord = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex spiritguard = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex earthwarden = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabaseShamanPvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public List<List> getSpecs() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getEarthwarden() {
        return earthwarden;
    }

}
