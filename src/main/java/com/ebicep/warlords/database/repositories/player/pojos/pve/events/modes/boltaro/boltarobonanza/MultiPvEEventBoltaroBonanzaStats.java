package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.MultiPvEEventBoltaroStats;

public interface MultiPvEEventBoltaroBonanzaStats extends MultiPvEEventBoltaroStats<
        PvEEventBoltaroBonanzaStatsWarlordsClasses,
        DatabaseGamePvEEventBoltaroBonanza,
        DatabaseGamePlayerPvEEventBoltaroBonanza,
        PvEEventBoltaroBonanzaStats,
        PvEEventBoltaroBonanzaStatsWarlordsSpecs>,
        PvEEventBoltaroBonanzaStats {

    @Override
    default int getHighestSplit() {
        return getStat(PvEEventBoltaroBonanzaStats::getHighestSplit, Integer::max, 0);
    }

}
