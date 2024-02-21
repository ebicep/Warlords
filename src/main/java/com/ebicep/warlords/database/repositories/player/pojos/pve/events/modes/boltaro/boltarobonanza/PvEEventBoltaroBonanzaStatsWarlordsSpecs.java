package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.PvEEventBoltaroStatsWarlordsSpecs;

public interface PvEEventBoltaroBonanzaStatsWarlordsSpecs extends PvEEventBoltaroStatsWarlordsSpecs<
        DatabaseGamePvEEventBoltaroBonanza,
        DatabaseGamePlayerPvEEventBoltaroBonanza,
        PvEEventBoltaroBonanzaStats> {


//    @Override
//    default long getMostDamageInRound() {
//        return Arrays.stream(getSpecs())
//                     .map(PvEStats::getMostDamageInRound)
//                     .max(Long::compareTo)
//                     .orElse(0L);
//    }
//
//    @Override
//    default void setMostDamageInRound(long mostDamageInRound) {
//        // only set for specs
//    }

}
