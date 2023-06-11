package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.DatabaseBasePvEEventSpidersDwelling;

public class DatabaseDruidPvEEventSpidersDwelling extends DatabaseBasePvEEventSpidersDwelling implements DatabaseWarlordsSpecs {

    private DatabaseBasePvEEventSpidersDwelling conjurer = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling guardian = new DatabaseBasePvEEventSpidersDwelling();
    private DatabaseBasePvEEventSpidersDwelling priest = new DatabaseBasePvEEventSpidersDwelling();

    public DatabaseDruidPvEEventSpidersDwelling() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEEventSpidersDwelling[]{conjurer, guardian, priest};
    }


    public DatabaseBasePvEEventSpidersDwelling getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEEventSpidersDwelling getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEEventSpidersDwelling getPriest() {
        return priest;
    }

}
