
package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;


import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabaseBaseTDM;

public class DatabaseArcanistTDM extends DatabaseBaseTDM implements DatabaseWarlordsSpecs {

    private DatabaseBaseTDM conjurer = new DatabaseBaseTDM();
    private DatabaseBaseTDM sentinel = new DatabaseBaseTDM();
    private DatabaseBaseTDM luminary = new DatabaseBaseTDM();

    public DatabaseArcanistTDM() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBaseTDM[]{conjurer, sentinel, luminary};
    }


    public DatabaseBaseTDM getConjurer() {
        return conjurer;
    }

    public DatabaseBaseTDM getSentinel() {
        return sentinel;
    }

    public DatabaseBaseTDM getLuminary() {
        return luminary;
    }

}
