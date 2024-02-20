
package com.ebicep.warlords.database.repositories.player.pojos.tdm.classes;


import com.ebicep.warlords.database.repositories.player.pojos.tdm.TDMStatsWarlordsSpecs;

import java.util.List;

public class DatabaseArcanistTDM implements TDMStatsWarlordsSpecs {

    private DatabaseBaseTDM conjurer = new DatabaseBaseTDM();
    private DatabaseBaseTDM sentinel = new DatabaseBaseTDM();
    private DatabaseBaseTDM luminary = new DatabaseBaseTDM();

    public DatabaseArcanistTDM() {
        super();
    }

    @Override
    public List<List<DatabaseBaseTDM>> getSpecs() {
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
