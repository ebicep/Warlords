package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyStatsWarlordsSpecs;

public class DatabaseShamanPvEAnomaly implements AnomalyStatsWarlordsSpecs {
    protected AnomalyDatabaseStatInformation thunderlord = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation spiritguard = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation earthwarden = new AnomalyDatabaseStatInformation();

    @Override
    public AnomalyDatabaseStatInformation[] getSpecs() {
        return new AnomalyDatabaseStatInformation[]{thunderlord, spiritguard, earthwarden};
    }
}
