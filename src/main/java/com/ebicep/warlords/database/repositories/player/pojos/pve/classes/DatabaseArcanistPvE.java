
package com.ebicep.warlords.database.repositories.player.pojos.pve.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabaseBasePvE;

public class DatabaseArcanistPvE extends DatabaseBasePvE implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvE conjurer = new DatabaseBasePvE();
    protected DatabaseBasePvE sentinel = new DatabaseBasePvE();
    protected DatabaseBasePvE luminary = new DatabaseBasePvE();

    public DatabaseArcanistPvE() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvE[]{conjurer, sentinel, luminary};
    }

    public DatabaseBasePvE getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvE getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvE getLuminary() {
        return luminary;
    }

}
