package com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePlayerPvEEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public class DatabaseGamePlayerPvEEventNarmersTomb extends DatabaseGamePlayerPvEEvent {

    public DatabaseGamePlayerPvEEventNarmersTomb() {
    }

    public DatabaseGamePlayerPvEEventNarmersTomb(
            WarlordsPlayer warlordsPlayer,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption
    ) {
        super(warlordsPlayer, waveDefenseOption, eventPointsOption);
    }
}
