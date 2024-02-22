package com.ebicep.warlords.database.repositories.games.pojos.pve.events;

import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.TimeElapsed;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Document(collection = "Games_Information_Event_PvE")
public abstract class DatabaseGamePvEEvent<T extends DatabaseGamePlayerPvEEvent> extends DatabaseGamePvEBase<T> implements TimeElapsed {

    public DatabaseGamePvEEvent() {
    }

    public DatabaseGamePvEEvent(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
    }

    public abstract GameEvents getEvent();

    public int getPointLimit() {
        return 100_000;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    @Override
    public Team getTeam(DatabaseGamePlayerBase player) {
        return Team.BLUE;
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return DatabaseGamePlayerResult.NONE;
    }

    public abstract List<DatabaseGamePlayerPvEEvent> getPlayers();

}
