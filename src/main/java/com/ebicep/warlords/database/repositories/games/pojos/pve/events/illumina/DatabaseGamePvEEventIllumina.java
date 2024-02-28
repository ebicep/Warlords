package com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;

public abstract class DatabaseGamePvEEventIllumina<T extends DatabaseGamePlayerPvEEventIllumina> extends DatabaseGamePvEEvent<T> {

    protected DatabaseGamePvEEventIllumina() {
    }

    public DatabaseGamePvEEventIllumina(Game game, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
    }
}
