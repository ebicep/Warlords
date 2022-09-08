package com.ebicep.warlords.database.repositories.player.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayersTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class TDMDatabaseStatInformation extends AbstractDatabaseStatInformation {

    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public TDMDatabaseStatInformation() {
    }

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier
    ) {
        assert databaseGame instanceof DatabaseGameTDM;
        assert gamePlayer instanceof DatabaseGamePlayersTDM.DatabaseGamePlayerTDM;

        this.totalTimePlayed += (long) (900 - ((DatabaseGameTDM) databaseGame).getTimeLeft()) * multiplier;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }
}
