
package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

public class DatabaseArcanistPvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEWaveDefense conjurer = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense sentinel = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense cleric = new DatabaseBasePvEWaveDefense();

    public DatabaseArcanistPvEWaveDefense() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{conjurer, sentinel, cleric};
    }

    public DatabaseBasePvEWaveDefense getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEWaveDefense getSentinel() {
        return sentinel;
    }

    public DatabaseBasePvEWaveDefense getCleric() {
        return cleric;
    }

}
