
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabaseBasePvEEventNarmersTomb;

public class DatabaseDruidPvEEventNarmersTomb extends DatabaseBasePvEEventNarmersTomb implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmersTomb conjurer = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb guardian = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb priest = new DatabaseBasePvEEventNarmersTomb();

    public DatabaseDruidPvEEventNarmersTomb() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventNarmersTomb[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEEventNarmersTomb getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventNarmersTomb getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEventNarmersTomb getPriest() {
        return priest;
    }

}
