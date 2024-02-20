package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabaseBasePvEEventLibraryArchives;

public class DatabaseWarriorPvEEventLibraryArchives extends DatabaseBasePvEEventLibraryArchives implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventLibraryArchives berserker = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives defender = new DatabaseBasePvEEventLibraryArchives();
    private DatabaseBasePvEEventLibraryArchives revenant = new DatabaseBasePvEEventLibraryArchives();

    public DatabaseWarriorPvEEventLibraryArchives() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEEventLibraryArchives[]{berserker, defender, revenant};
    }


    public DatabaseBasePvEEventLibraryArchives getBerserker() {
        return berserker;
    }

    public DatabaseBasePvEEventLibraryArchives getDefender() {
        return defender;
    }

    public DatabaseBasePvEEventLibraryArchives getRevenant() {
        return revenant;
    }

}
