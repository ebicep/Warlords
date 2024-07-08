package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class PvEEventLibraryForgottenCodexDatabaseStatInformation
        extends PvEEventDatabaseStatInformation<DatabaseGamePvEEventForgottenCodex, DatabaseGamePlayerPvEEventForgottenCodex>
        implements PvEEventLibraryArchivesForgottenCodexStats {

    @Field("fastest_game_finished")
    protected long fastestGameFinished = 0;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventForgottenCodex databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventForgottenCodex gamePlayer,
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
