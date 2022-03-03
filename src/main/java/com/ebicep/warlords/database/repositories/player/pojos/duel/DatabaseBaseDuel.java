package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayersDuel;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseBaseDuel extends AbstractDatabaseStatInformation {

    @Field("total_time_played")
    private long totalTimePlayed = 0;

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
        assert databaseGame instanceof DatabaseGameDuel;
        assert gamePlayer instanceof DatabaseGamePlayersDuel.DatabaseGamePlayerDuel;

        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
        this.totalTimePlayed += 900 - ((DatabaseGameDuel) databaseGame).getTimeLeft();
    }
}
