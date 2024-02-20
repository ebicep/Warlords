package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;


import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStats;

public interface OnslaughtStats extends PvEStats<DatabaseGamePvEOnslaught, DatabaseGamePlayerPvEOnslaught> {

    long getLongestTicksLived();

}
