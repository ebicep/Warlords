package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsClasses;

public interface PvEEventStatsWarlordsClasses<DatabaseGameT extends DatabaseGamePvEEvent<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEEvent,
        T extends PvEEventStats<DatabaseGameT, DatabaseGamePlayerT>,
        R extends PvEEventStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, T>>
        extends PvEStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, T, R>,
        PvEEventStats<DatabaseGameT, DatabaseGamePlayerT> {

    @Override
    default long getEventPointsCumulative() {
        return getStat(PvEEventStats::getEventPointsCumulative, Long::sum, 0L);
    }

    @Override
    default long getHighestEventPointsGame() {
        return getStat(PvEEventStats::getHighestEventPointsGame, Math::max, 0L);
    }

    //
//    @Override
//    default long getMostDamageInRound() {
//        return Arrays.stream(Specializations.VALUES)
//                     .map(this::getSpec)
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
