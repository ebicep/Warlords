package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;


import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.pojos.pve.MultiPvEStats;

public interface MultiPvEOnslaughtStats extends MultiPvEStats<
        OnslaughtStatsWarlordsClasses,
        DatabaseGamePvEOnslaught,
        DatabaseGamePlayerPvEOnslaught,
        OnslaughtStats,
        OnslaughtStatsWarlordsSpecs> {


}
