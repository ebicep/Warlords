package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyStatsWarlordsSpecs;

public class DatabasePaladinPvEAnomaly implements AnomalyStatsWarlordsSpecs {
    protected AnomalyDatabaseStatInformation avenger = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation crusader = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation protector = new AnomalyDatabaseStatInformation();

    @Override
    public AnomalyDatabaseStatInformation[] getSpecs() {
        return new AnomalyDatabaseStatInformation[]{avenger, crusader, protector};
    }
}
