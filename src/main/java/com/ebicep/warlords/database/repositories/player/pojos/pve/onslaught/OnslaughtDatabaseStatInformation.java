package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;

public class OnslaughtDatabaseStatInformation extends PvEDatabaseStatInformation {

    @Override
    public void updateCustomStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        if (gamePlayer instanceof DatabaseGamePlayerPvEOnslaught) {
            DatabaseGamePlayerPvEOnslaught onslaughtGamePlayer = (DatabaseGamePlayerPvEOnslaught) gamePlayer;
            onslaughtGamePlayer.getSyntheticPouch().forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount * multiplier));
            onslaughtGamePlayer.getAspirantPouch().forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount * multiplier));
        }
    }
}
