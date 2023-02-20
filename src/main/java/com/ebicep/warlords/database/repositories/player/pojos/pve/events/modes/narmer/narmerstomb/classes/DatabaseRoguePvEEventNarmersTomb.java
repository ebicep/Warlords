package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabaseBasePvEEventNarmersTomb;

public class DatabaseRoguePvEEventNarmersTomb extends DatabaseBasePvEEventNarmersTomb implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventNarmersTomb assassin = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb vindicator = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb apothecary = new DatabaseBasePvEEventNarmersTomb();

    public DatabaseRoguePvEEventNarmersTomb() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventNarmersTomb[]{assassin, vindicator, apothecary};
    }


    public DatabaseBasePvEEventNarmersTomb getAssassin() {
        return assassin;
    }

    public DatabaseBasePvEEventNarmersTomb getVindicator() {
        return vindicator;
    }

    public DatabaseBasePvEEventNarmersTomb getApothecary() {
        return apothecary;
    }
}
