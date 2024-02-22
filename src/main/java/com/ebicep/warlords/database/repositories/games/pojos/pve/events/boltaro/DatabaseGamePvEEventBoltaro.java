package com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;

public abstract class DatabaseGamePvEEventBoltaro extends DatabaseGamePvEEvent {

    public DatabaseGamePvEEventBoltaro(Game game, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
    }

    protected DatabaseGamePvEEventBoltaro() {
    }
}
