package com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives;

import com.ebicep.warlords.database.repositories.games.pojos.pve.events.DatabaseGamePvEEvent;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;

public abstract class DatabaseGamePvEEventLibraryArchives extends DatabaseGamePvEEvent {

    public DatabaseGamePvEEventLibraryArchives(Game game, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
    }

    protected DatabaseGamePvEEventLibraryArchives() {
    }
}
