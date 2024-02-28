package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;

public interface PvEEventStatsWarlordsSpecs<
        DatabaseGameT extends DatabaseGamePvEEvent<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEEvent,
        T extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT>>
        extends PvEStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T> {


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
