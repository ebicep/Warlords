package com.ebicep.warlords.database.repositories.player.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayersTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseBaseTDM extends AbstractDatabaseStatInformation {

    @Field("total_time_played")
    private long totalTimePlayed = 0;

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean isCompGame, boolean add) {
        assert databaseGame instanceof DatabaseGameTDM;
        assert gamePlayer instanceof DatabaseGamePlayersTDM.DatabaseGamePlayerTDM;

        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
        this.totalTimePlayed += 900 - ((DatabaseGameTDM) databaseGame).getTimeLeft();
    }
}
