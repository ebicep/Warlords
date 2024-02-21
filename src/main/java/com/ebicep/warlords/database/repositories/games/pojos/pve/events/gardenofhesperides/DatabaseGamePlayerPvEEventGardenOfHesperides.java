package com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public abstract class DatabaseGamePlayerPvEEventGardenOfHesperides extends DatabaseGamePlayerPvEEvent {


    public DatabaseGamePlayerPvEEventGardenOfHesperides(
            WarlordsPlayer warlordsPlayer,
            WarlordsGameTriggerWinEvent gameWinEvent,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption,
            boolean counted
    ) {
    }

    protected DatabaseGamePlayerPvEEventGardenOfHesperides() {
    }
}
