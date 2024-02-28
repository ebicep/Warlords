package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventGardenOfHesperidesTartarusDatabaseStatInformation extends PvEEventDatabaseStatInformation<DatabaseGamePvEEventTartarus, DatabaseGamePlayerPvEEventTartarus> implements PvEEventGardenOfHesperidesTartarusStats {

    @Field("fastest_game_finished")
    protected long fastestGameFinished = 0;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventTartarus databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventTartarus gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        boolean won = databaseGame.getWavesCleared() == 1;
        int timeElapsed = databaseGame.getTimeElapsed();
        if (multiplier > 0) {
            if (won && (this.fastestGameFinished == 0 || timeElapsed < fastestGameFinished)) {
                this.fastestGameFinished = timeElapsed;
            }
        } else if (this.fastestGameFinished == timeElapsed) {
            this.fastestGameFinished = 0;
        }
    }

    @Override
    public long getFastestGameFinished() {
        return fastestGameFinished;
    }
}
