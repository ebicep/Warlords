package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.player.pojos.DatabaseWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.player.general.Specializations;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface PvEStatsWarlordsClasses<T extends PvEStats, R extends DatabaseWarlordsSpecs<T>> extends PvEStats, StatsWarlordsClasses<T, R> {

    @Nonnull
    private HashMap<String, Long> getStat(Function<PvEStats, Map<String, Long>> statFunction) {
        return Arrays.stream(Specializations.VALUES)
                     .map(this::getSpec)
                     .map(statFunction)
                     .collect(
                             HashMap::new,
                             (m, v) -> v.forEach((k, w) -> m.merge(k, w, Long::sum)),
                             (map1, map2) -> map2.forEach((k, v) -> map1.merge(k, v, Long::sum))
                     );
    }

    @Override
    default Map<String, Long> getMobKills() {
        return getStat(PvEStats::getMobKills);
    }

    @Override
    default Map<String, Long> getMobAssists() {
        return getStat(PvEStats::getMobAssists);
    }

    @Override
    default Map<String, Long> getMobDeaths() {
        return getStat(PvEStats::getMobDeaths);
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
