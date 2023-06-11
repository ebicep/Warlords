
package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;

public class DatabaseDruidPvE extends DatabaseBasePvE implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvE conjurer = new DatabaseBasePvE();
    protected DatabaseBasePvE guardian = new DatabaseBasePvE();
    protected DatabaseBasePvE priest = new DatabaseBasePvE();

    public DatabaseDruidPvE() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvE[]{conjurer, guardian, priest};
    }

    public DatabaseBasePvE getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvE getGuardian() {
        return guardian;
    }

    public DatabaseBasePvE getPriest() {
        return priest;
    }

}
