package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;

public class OnslaughtDatabaseStatInformation extends PvEDatabaseStatInformation {

    private long longestTicksLived;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEOnslaught;
        assert gamePlayer instanceof DatabaseGamePlayerPvEOnslaught;
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        if (multiplier > 0) {
            this.longestTicksLived = Math.max(((long) ((DatabaseGamePvEOnslaught) databaseGame).getTimeElapsed() * multiplier), longestTicksLived);
        } else if (this.longestTicksLived == ((DatabaseGamePvEOnslaught) databaseGame).getTimeElapsed()) {
            this.longestTicksLived = 0;
        }
    }

    public long getLongestTicksLived() {
        return longestTicksLived;
    }
}
