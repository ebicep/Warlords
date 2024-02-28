package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.PvEEventBoltaroStatsWarlordsSpecs;

public interface PvEEventBoltaroLairStatsWarlordsSpecs extends PvEEventBoltaroStatsWarlordsSpecs<DatabaseGamePvEEventBoltaroLair, DatabaseGamePlayerPvEEventBoltarosLair, PvEEventBoltaroLairStats> {


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
