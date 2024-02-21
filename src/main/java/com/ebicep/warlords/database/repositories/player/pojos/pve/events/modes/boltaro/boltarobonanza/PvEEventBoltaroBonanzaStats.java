package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.PvEEventBoltaroStats;

public interface PvEEventBoltaroBonanzaStats extends PvEEventBoltaroStats<DatabaseGamePvEEventBoltaroBonanza, DatabaseGamePlayerPvEEventBoltaroBonanza> {

    int getHighestSplit();

}
