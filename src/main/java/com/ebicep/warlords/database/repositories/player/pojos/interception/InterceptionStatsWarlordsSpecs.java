package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayerInterception;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.interception.classes.DatabaseBaseInterception;

public interface InterceptionStatsWarlordsSpecs extends StatsWarlordsSpecs<DatabaseGameInterception, DatabaseGamePlayerInterception, DatabaseBaseInterception> {
}
