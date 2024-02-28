package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;

public interface DuelStats extends Stats<DatabaseGameDuel, DatabaseGamePlayerDuel> {

    long getTotalTimePlayed();

}
