package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.DatabaseBasePvEEventLibraryForgottenCodex;

public class DatabaseWarriorPvEEventLibraryForgottenCodex extends DatabaseBasePvEEventLibraryForgottenCodex implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryForgottenCodex berserker = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex defender = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex revenant = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabaseWarriorPvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventLibraryForgottenCodex[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventLibraryForgottenCodex getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventLibraryForgottenCodex getRevenant() {
        return revenant;
    }

}
