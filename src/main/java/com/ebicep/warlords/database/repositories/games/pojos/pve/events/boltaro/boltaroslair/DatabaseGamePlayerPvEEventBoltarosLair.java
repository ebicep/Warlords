package com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public class DatabaseGamePlayerPvEEventBoltarosLair extends DatabaseGamePlayerPvEEvent {

    public DatabaseGamePlayerPvEEventBoltarosLair() {
    }

    public DatabaseGamePlayerPvEEventBoltarosLair(
            WarlordsPlayer warlordsPlayer,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption
    ) {
        super(warlordsPlayer, waveDefenseOption, eventPointsOption);
    }
}
