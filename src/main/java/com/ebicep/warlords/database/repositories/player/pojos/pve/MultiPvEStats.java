package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStats;
import com.ebicep.warlords.player.general.Specializations;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public interface MultiPvEStats<
        StatsWarlordsClassesT extends PvEStatsWarlordsClasses<DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        DatabaseGameT extends DatabaseGamePvEBase<DatabaseGamePlayerT>,
        DatabaseGamePlayerT extends DatabaseGamePlayerPvEBase,
        StatsT extends PvEStats<DatabaseGameT, DatabaseGamePlayerT>,
        SpecsT extends PvEStatsWarlordsSpecs<DatabaseGameT, DatabaseGamePlayerT, StatsT>>
        extends MultiStats<StatsWarlordsClassesT, DatabaseGameT, DatabaseGamePlayerT, StatsT, SpecsT>,
        PvEStats<DatabaseGameT, DatabaseGamePlayerT> {

    @Nonnull
    private HashMap<String, Long> getStat(Function<PvEStats<DatabaseGameT, DatabaseGamePlayerT>, Map<String, Long>> statFunction) {
        return Arrays.stream(Specializations.VALUES)
                     .flatMap(s -> getStats().stream().map(statFunction))
                     .collect(
                             HashMap::new,
                             (m, v) -> v.forEach((k, w) -> m.merge(k, w, Long::sum)),
                             (map1, map2) -> map2.forEach((k, v) -> map1.merge(k, v, Long::sum))
                     );
    }

    @Override
    default long getTotalTimePlayed() {
        return getStat(PvEStats::getTotalTimePlayed, Long::sum, 0L);
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
}
