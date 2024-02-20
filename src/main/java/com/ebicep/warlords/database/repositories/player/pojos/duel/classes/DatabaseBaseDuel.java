package com.ebicep.warlords.database.repositories.player.pojos.duel.classes;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DuelDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;

public class DatabaseBaseDuel extends DuelDatabaseStatInformation {

    public DatabaseBaseDuel() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameDuel databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerDuel gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.experience += gamePlayer.getExperienceEarnedSpec() * multiplier;
    }
}
