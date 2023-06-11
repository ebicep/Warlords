
package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseDruidTDM extends DatabaseBaseTDM implements DatabaseWarlordsSpecs {

    private DatabaseBaseTDM conjurer = new DatabaseBaseTDM();
    private DatabaseBaseTDM guardian = new DatabaseBaseTDM();
    private DatabaseBaseTDM priest = new DatabaseBaseTDM();

    public DatabaseDruidTDM() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseTDM[]{conjurer, guardian, priest};
    }


    public DatabaseBaseTDM getConjurer() {
        return conjurer;
    }

    public DatabaseBaseTDM getGuardian() {
        return guardian;
    }

    public DatabaseBaseTDM getPriest() {
        return priest;
    }

}
