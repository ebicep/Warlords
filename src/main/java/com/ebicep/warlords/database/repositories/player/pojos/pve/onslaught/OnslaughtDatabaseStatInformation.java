package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;

public class OnslaughtDatabaseStatInformation extends PvEDatabaseStatInformation<DatabaseGamePvEOnslaught, DatabaseGamePlayerPvEOnslaught> implements OnslaughtStats {

    private long longestTicksLived; // change field name

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEOnslaught databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEOnslaught gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        if (multiplier > 0) {
            this.longestTicksLived = Math.max(((long) databaseGame.getTimeElapsed() * multiplier), longestTicksLived);
        } else if (this.longestTicksLived == databaseGame.getTimeElapsed()) {
            this.longestTicksLived = 0;
        }
    }

    public long getLongestTicksLived() {
        return longestTicksLived;
    }
}
