
package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb.DatabaseBasePvEEventNarmersTomb;

public class DatabaseArcanistPvEEventNarmersTomb extends DatabaseBasePvEEventNarmersTomb implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventNarmersTomb conjurer = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb sentinel = new DatabaseBasePvEEventNarmersTomb();
    private DatabaseBasePvEEventNarmersTomb cleric = new DatabaseBasePvEEventNarmersTomb();

    public DatabaseArcanistPvEEventNarmersTomb() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventNarmersTomb[]{conjurer, sentinel, cleric};
    }


    public DatabaseBasePvEEventNarmersTomb getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventNarmersTomb getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEEventNarmersTomb getCleric() {
        return cleric;
    }

}
