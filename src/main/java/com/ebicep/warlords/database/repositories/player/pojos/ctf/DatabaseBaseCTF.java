package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.game.GameMode;

public class DatabaseBaseCTF extends CTFDatabaseStatInformation {

    @Override
    public void updateCustomStats(
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier
    ) {
        super.updateCustomStats(databaseGame, gameMode, gamePlayer, result, multiplier);

        //UPDATE SPEC EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedSpec() * multiplier;
    }

}
