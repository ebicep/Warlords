package com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;

public abstract class DatabaseGamePvEEventMithra extends DatabaseGamePvEEvent {

    public DatabaseGamePvEEventMithra(Game game, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
    }

    protected DatabaseGamePvEEventMithra() {
    }
}
