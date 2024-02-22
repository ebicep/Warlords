package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.classes;


import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex.PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs;

public class DatabaseWarriorPvEEventLibraryForgottenCodex implements PvEEventLibraryArchivesForgottenCodexStatsWarlordsSpecs {

    private DatabaseBasePvEEventLibraryForgottenCodex berserker = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex defender = new DatabaseBasePvEEventLibraryForgottenCodex();
    private DatabaseBasePvEEventLibraryForgottenCodex revenant = new DatabaseBasePvEEventLibraryForgottenCodex();

    public DatabaseWarriorPvEEventLibraryForgottenCodex() {
        super();
    }

    @Override
    public DatabaseBasePvEEventLibraryForgottenCodex[] getSpecs() {
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
