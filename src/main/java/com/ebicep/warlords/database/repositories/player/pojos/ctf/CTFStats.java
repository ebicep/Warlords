package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;

public interface CTFStats extends Stats<DatabaseGameCTF, DatabaseGamePlayerCTF> {

    int getFlagsCaptured();

    int getFlagsReturned();

    long getTotalBlocksTravelled();

    long getMostBlocksTravelled();

    long getTotalTimeInRespawn();

    long getTotalTimePlayed();

}
