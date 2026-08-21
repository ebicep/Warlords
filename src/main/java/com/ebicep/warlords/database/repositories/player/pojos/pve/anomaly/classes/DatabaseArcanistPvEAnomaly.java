package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyStatsWarlordsSpecs;

public class DatabaseArcanistPvEAnomaly implements AnomalyStatsWarlordsSpecs {
    protected AnomalyDatabaseStatInformation conjurer = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation sentinel = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation luminary = new AnomalyDatabaseStatInformation();

    @Override
    public AnomalyDatabaseStatInformation[] getSpecs() {
        return new AnomalyDatabaseStatInformation[]{conjurer, sentinel, luminary};
    }
}
