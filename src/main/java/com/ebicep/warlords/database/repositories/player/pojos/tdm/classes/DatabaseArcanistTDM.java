
package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;


import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseArcanistTDM extends DatabaseBaseTDM implements DatabaseWarlordsSpecs {

    private DatabaseBaseTDM conjurer = new DatabaseBaseTDM();
    private DatabaseBaseTDM sentinel = new DatabaseBaseTDM();
    private DatabaseBaseTDM cleric = new DatabaseBaseTDM();

    public DatabaseArcanistTDM() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBaseTDM[]{conjurer, sentinel, cleric};
    }


    public DatabaseBaseTDM getConjurer() {
        return conjurer;
    }

    public DatabaseBaseTDM getSentinel() {
        return sentinel;
    }

    public DatabaseBaseTDM getCleric() {
        return cleric;
    }

}
