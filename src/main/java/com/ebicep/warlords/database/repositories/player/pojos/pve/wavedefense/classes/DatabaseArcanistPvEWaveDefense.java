
package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

public class DatabaseArcanistPvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEWaveDefense conjurer = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense sentinel = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense luminary = new DatabaseBasePvEWaveDefense();

    public DatabaseArcanistPvEWaveDefense() {
        super();
    }

    @Override
    public Stats[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{conjurer, sentinel, luminary};
    }

    public DatabaseBasePvEWaveDefense getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEWaveDefense getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEWaveDefense getLuminary() {
        return luminary;
    }

}
