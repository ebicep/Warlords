package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly;

public class DatabaseClassPvEAnomaly implements AnomalyStatsWarlordsSpecs {

    private AnomalyDatabaseStatInformation specOne = new AnomalyDatabaseStatInformation();
    private AnomalyDatabaseStatInformation specTwo = new AnomalyDatabaseStatInformation();
    private AnomalyDatabaseStatInformation specThree = new AnomalyDatabaseStatInformation();

    @Override
    public AnomalyDatabaseStatInformation[] getSpecs() {
        return new AnomalyDatabaseStatInformation[]{specOne, specTwo, specThree};
    }
}
