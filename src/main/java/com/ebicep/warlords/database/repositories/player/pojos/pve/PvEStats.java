package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.player.pojos.Stats;

import java.util.Map;

public interface PvEStats extends Stats {

    Map<String, Long> getMobKills();

    Map<String, Long> getMobAssists();

    Map<String, Long> getMobDeaths();

    long getMostDamageInRound();

    void setMostDamageInRound(long mostDamageInRound);

}
