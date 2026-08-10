package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly;

import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePlayerPvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePvEAnomaly;
import com.ebicep.warlords.database.repositories.player.pojos.pve.MultiPvEStats;

public interface MultiPvEAnomalyStats extends MultiPvEStats<
        AnomalyStatsWarlordsClasses,
        DatabaseGamePvEAnomaly,
        DatabaseGamePlayerPvEAnomaly,
        AnomalyStats,
        AnomalyStatsWarlordsSpecs>,
        AnomalyStats {
}
