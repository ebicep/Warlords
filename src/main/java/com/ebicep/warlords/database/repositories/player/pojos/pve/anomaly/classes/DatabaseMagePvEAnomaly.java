package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyStatsWarlordsSpecs;

public class DatabaseMagePvEAnomaly implements AnomalyStatsWarlordsSpecs {
    protected AnomalyDatabaseStatInformation pyromancer = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation cryomancer = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation aquamancer = new AnomalyDatabaseStatInformation();

    @Override
    public AnomalyDatabaseStatInformation[] getSpecs() {
        return new AnomalyDatabaseStatInformation[]{pyromancer, cryomancer, aquamancer};
    }
}
