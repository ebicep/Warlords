package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.PvEEventLibraryArchivesDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class PvEEventLibraryForgottenCodexDatabaseStatInformation extends PvEEventLibraryArchivesDatabaseStatInformation {

    @Field("fastest_game_finished")
    protected long fastestGameFinished = 0;

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEEventForgottenCodex;
        assert gamePlayer instanceof DatabaseGamePlayerPvEEventForgottenCodex;
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        DatabaseGamePvEEventForgottenCodex eventForgottenCodex = (DatabaseGamePvEEventForgottenCodex) databaseGame;
        boolean won = eventForgottenCodex.getWavesCleared() == 1;
        int timeElapsed = eventForgottenCodex.getTimeElapsed();
        if (multiplier > 0) {
            if (won && (this.fastestGameFinished == 0 || timeElapsed < fastestGameFinished)) {
                this.fastestGameFinished = timeElapsed;
            }
        } else if (this.fastestGameFinished == timeElapsed) {
            this.fastestGameFinished = 0;
        }
    }

    public long getExperiencePvE() {
        return experiencePvE;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public Map<String, Long> getMobKills() {
        return mobKills;
    }

    public Map<String, Long> getMobAssists() {
        return mobAssists;
    }

    public Map<String, Long> getMobDeaths() {
        return mobDeaths;
    }

    public long getFastestGameFinished() {
        return fastestGameFinished;
    }
}
