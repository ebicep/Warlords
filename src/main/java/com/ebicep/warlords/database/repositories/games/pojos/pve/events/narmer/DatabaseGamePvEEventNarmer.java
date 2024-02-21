package com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;

public abstract class DatabaseGamePvEEventNarmer extends DatabaseGamePvEEvent {

    public DatabaseGamePvEEventNarmer(Game game, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
    }

    protected DatabaseGamePvEEventNarmer() {
    }
}
