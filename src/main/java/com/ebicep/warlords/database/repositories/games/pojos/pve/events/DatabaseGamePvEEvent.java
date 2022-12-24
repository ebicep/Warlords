package com.ebicep.warlords.database.repositories.games.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;

public abstract class DatabaseGamePvEEvent extends DatabaseGameBase {

    @Field("time_elapsed")
    private int timeElapsed;

    public DatabaseGamePvEEvent() {
    }

    public DatabaseGamePvEEvent(@Nonnull Game game, boolean counted) {
        super(game, counted);
        this.timeElapsed = RecordTimeElapsedOption.getTicksElapsed(game);
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

}
