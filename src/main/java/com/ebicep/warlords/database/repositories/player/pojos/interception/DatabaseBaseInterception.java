package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayersInterception;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseBaseInterception extends AbstractDatabaseStatInformation {

    @Field("total_time_played")
    private long totalTimePlayed = 0;

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
        assert databaseGame instanceof DatabaseGameInterception;
        assert gamePlayer instanceof DatabaseGamePlayersInterception.DatabaseGamePlayerInterception;

        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
        this.totalTimePlayed += 900 - ((DatabaseGameInterception) databaseGame).getTimeLeft();
    }
}
