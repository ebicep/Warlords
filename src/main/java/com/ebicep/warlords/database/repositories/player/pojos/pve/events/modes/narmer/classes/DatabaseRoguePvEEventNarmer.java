package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.DatabaseBasePvEEventNarmer;

public class DatabaseRoguePvEEventNarmer extends DatabaseBasePvEEventNarmer implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventNarmer assassin = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer vindicator = new DatabaseBasePvEEventNarmer();
    private DatabaseBasePvEEventNarmer apothecary = new DatabaseBasePvEEventNarmer();

    public DatabaseRoguePvEEventNarmer() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventNarmer[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventNarmer getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventNarmer getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventNarmer getApothecary() {
        return apothecary;
    }
}
