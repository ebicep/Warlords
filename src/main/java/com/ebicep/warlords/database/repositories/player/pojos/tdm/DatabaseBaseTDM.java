package com.ebicep.warlords.database.repositories.player.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.game.GameMode;

public class DatabaseBaseTDM extends TDMDatabaseStatInformation {

    @Override
    public void updateCustomStats(DatabaseGameBase databaseGame, GameMode gameMode, DatabaseGamePlayerBase gamePlayer, DatabaseGamePlayerResult result, boolean add) {
        super.updateCustomStats(databaseGame, gameMode, gamePlayer, result, add);

        //UPDATE SPEC EXPERIENCE
        this.experience += add ? gamePlayer.getExperienceEarnedSpec() : -gamePlayer.getExperienceEarnedSpec();
    }
}
