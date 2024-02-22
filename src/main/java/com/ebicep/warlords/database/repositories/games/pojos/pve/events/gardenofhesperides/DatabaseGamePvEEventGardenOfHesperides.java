package com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;

public abstract class DatabaseGamePvEEventGardenOfHesperides extends DatabaseGamePvEEvent {

    public DatabaseGamePvEEventGardenOfHesperides(Game game, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
    }

    protected DatabaseGamePvEEventGardenOfHesperides() {
    }
}
