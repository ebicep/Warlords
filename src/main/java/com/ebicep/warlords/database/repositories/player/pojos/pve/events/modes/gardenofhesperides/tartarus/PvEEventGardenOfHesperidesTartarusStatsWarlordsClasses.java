package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus;


import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.PvEEventGardenOfHesperidesStatsWarlordsClasses;

public interface PvEEventGardenOfHesperidesTartarusStatsWarlordsClasses extends PvEEventGardenOfHesperidesStatsWarlordsClasses<
        DatabaseGamePvEEventTartarus,
        DatabaseGamePlayerPvEEventTartarus,
        PvEEventGardenOfHesperidesTartarusStats,
        PvEEventGardenOfHesperidesTartarusStatsWarlordsSpecs>,
        PvEEventGardenOfHesperidesTartarusStats {

    @Override
    default long getFastestGameFinished() {
        return getStat(PvEEventGardenOfHesperidesTartarusStats::getFastestGameFinished, Long::min, Long.MAX_VALUE);
    }
}
