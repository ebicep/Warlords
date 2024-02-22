package com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public abstract class DatabaseGamePlayerPvEEventBoltaro extends DatabaseGamePlayerPvEEvent {


    public DatabaseGamePlayerPvEEventBoltaro(
            WarlordsPlayer warlordsPlayer,
            WarlordsGameTriggerWinEvent gameWinEvent,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption,
            boolean counted
    ) {
        super(warlordsPlayer, gameWinEvent, waveDefenseOption, eventPointsOption, counted);
    }

    protected DatabaseGamePlayerPvEEventBoltaro() {
    }
}
