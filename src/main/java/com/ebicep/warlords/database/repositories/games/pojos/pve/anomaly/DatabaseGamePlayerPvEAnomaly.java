package com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public class DatabaseGamePlayerPvEAnomaly extends DatabaseGamePlayerPvEBase {

    public DatabaseGamePlayerPvEAnomaly() {
    }

    public DatabaseGamePlayerPvEAnomaly(
            WarlordsPlayer warlordsPlayer,
            WarlordsGameTriggerWinEvent gameWinEvent,
            PveOption pveOption,
            boolean counted
    ) {
        super(warlordsPlayer, gameWinEvent, pveOption, counted);
    }
}
