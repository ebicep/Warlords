package com.ebicep.warlords.database.repositories.player.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayerTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;

public interface TDMStats extends Stats<DatabaseGameTDM, DatabaseGamePlayerTDM> {

    long getTotalTimePlayed();

}
