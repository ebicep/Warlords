package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly;

import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePlayerPvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePvEAnomaly;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsClasses;

public interface AnomalyStatsWarlordsClasses extends PvEStatsWarlordsClasses<
        DatabaseGamePvEAnomaly,
        DatabaseGamePlayerPvEAnomaly,
        AnomalyStats,
        AnomalyStatsWarlordsSpecs>,
        AnomalyStats {
}
