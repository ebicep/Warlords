package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.MultiPvEEventGardenOfHesperidesStats;

public interface MultiPvEEventGardenOfHesperidesTartarusStats extends MultiPvEEventGardenOfHesperidesStats<
        PvEEventGardenOfHesperidesTartarusStatsWarlordsClasses,
        DatabaseGamePvEEventTartarus,
        DatabaseGamePlayerPvEEventTartarus,
        PvEEventGardenOfHesperidesTartarusStats,
        PvEEventGardenOfHesperidesTartarusStatsWarlordsSpecs>,
        PvEEventGardenOfHesperidesTartarusStats {

    @Override
    default long getFastestGameFinished() {
        return getStat(PvEEventGardenOfHesperidesTartarusStats::getFastestGameFinished, Math::min, Long.MAX_VALUE);
    }

}
        