package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsClass;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabaseBasePvEEventNarmersTomb;

public class DatabaseShamanPvEEventNarmersTomb extends DatabaseBasePvEEventNarmersTomb implements DatabaseWarlordsClass {

    private DatabaseBasePvEEventNarmersTomb thunderlord = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb spiritguard = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb earthwarden = new DatabaseBasePvEEventNarmersTomb();

    public DatabaseShamanPvEEventNarmersTomb() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventNarmersTomb[]{thunderlord, spiritguard, earthwarden};
    }

    public DatabaseBasePvEEventNarmersTomb getThunderlord() {
        return thunderlord;
    }

    public DatabaseBasePvEEventNarmersTomb getSpiritguard() {
        return spiritguard;
    }

    public DatabaseBasePvEEventNarmersTomb getEarthwarden() {
        return earthwarden;
    }

}
