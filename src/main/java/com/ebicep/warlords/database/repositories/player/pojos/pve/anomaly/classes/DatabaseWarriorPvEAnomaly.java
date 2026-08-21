package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.classes;

import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly.AnomalyStatsWarlordsSpecs;

public class DatabaseWarriorPvEAnomaly implements AnomalyStatsWarlordsSpecs {
    protected AnomalyDatabaseStatInformation berserker = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation defender = new AnomalyDatabaseStatInformation();
    protected AnomalyDatabaseStatInformation revenant = new AnomalyDatabaseStatInformation();

    @Override
    public AnomalyDatabaseStatInformation[] getSpecs() {
        return new AnomalyDatabaseStatInformation[]{berserker, defender, revenant};
    }
}
