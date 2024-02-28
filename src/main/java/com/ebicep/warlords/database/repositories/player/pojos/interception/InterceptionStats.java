package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayerInterception;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;

public interface InterceptionStats extends Stats<DatabaseGameInterception, DatabaseGamePlayerInterception> {

    int getPointsCaptured();

    int getPointsDefended();

    long getTotalTimePlayed();

}
