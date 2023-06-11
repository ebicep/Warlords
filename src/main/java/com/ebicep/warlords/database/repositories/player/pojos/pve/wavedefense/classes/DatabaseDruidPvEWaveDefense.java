
package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes;

import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.DatabaseBasePvEWaveDefense;

public class DatabaseDruidPvEWaveDefense extends DatabaseBasePvEWaveDefense implements DatabaseWarlordsSpecs {

    protected DatabaseBasePvEWaveDefense conjurer = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense guardian = new DatabaseBasePvEWaveDefense();
    protected DatabaseBasePvEWaveDefense priest = new DatabaseBasePvEWaveDefense();

    public DatabaseDruidPvEWaveDefense() {
        super();
    }

    @Override
    public AbstractDatabaseStatInformation[] getSpecs() {
        return new DatabaseBasePvEWaveDefense[]{conjurer, guardian, priest};
    }

    public DatabaseBasePvEWaveDefense getConjurer() {
        return conjurer;
    }

    public DatabaseBasePvEWaveDefense getGuardian() {
        return guardian;
    }

    public DatabaseBasePvEWaveDefense getPriest() {
        return priest;
    }

}
