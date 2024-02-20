package com.ebicep.warlords.database.repositories.player.pojos.siege;


import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;

public interface SiegeStats extends Stats<DatabaseGameSiege, DatabaseGamePlayerSiege> {

    int getPointsCaptured();

    int getPointsCapturedFail();

    long getTimeOnPoint();

    int getPayloadsEscorted();

    int getPayloadsEscortedFail();

    int getPayloadsDefended();

    int getPayloadsDefendedFail();

    long getTimeOnPayloadEscorting();

    long getTimeOnPayloadDefending();

    long getTotalTimePlayed();

}
