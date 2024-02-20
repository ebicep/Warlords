package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;


import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsClasses;

public interface OnslaughtStatsWarlordsClasses extends PvEStatsWarlordsClasses<DatabaseGamePvEOnslaught, DatabaseGamePlayerPvEOnslaught, OnslaughtStats, StatsWarlordsSpecs<DatabaseGamePvEOnslaught, DatabaseGamePlayerPvEOnslaught, OnslaughtStats>>, OnslaughtStats {

    @Override
    default long getLongestTicksLived() {
        return getStat(OnslaughtStats::getLongestTicksLived, Math::max, 0L);
    }

}
