package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;

import java.util.Map;

public interface PvEStats<DatabaseGameT extends DatabaseGamePvEBase<DatabaseGamePlayerT>, DatabaseGamePlayerT extends DatabaseGamePlayerPvEBase>
        extends Stats<DatabaseGameT, DatabaseGamePlayerT> {

    long getTotalTimePlayed();

    Map<String, Long> getMobKills();

    Map<String, Long> getMobAssists();

    Map<String, Long> getMobDeaths();

//    long getMostDamageInRound();
//
//    void setMostDamageInRound(long mostDamageInRound);

}
