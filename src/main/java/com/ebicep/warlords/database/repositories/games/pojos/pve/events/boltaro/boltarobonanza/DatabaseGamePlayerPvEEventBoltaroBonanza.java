package com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public class DatabaseGamePlayerPvEEventBoltaroBonanza extends DatabaseGamePlayerPvEEvent {

    public DatabaseGamePlayerPvEEventBoltaroBonanza() {
    }

    public DatabaseGamePlayerPvEEventBoltaroBonanza(
            WarlordsPlayer warlordsPlayer,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption
    ) {
        super(warlordsPlayer, waveDefenseOption, eventPointsOption);
    }
}
