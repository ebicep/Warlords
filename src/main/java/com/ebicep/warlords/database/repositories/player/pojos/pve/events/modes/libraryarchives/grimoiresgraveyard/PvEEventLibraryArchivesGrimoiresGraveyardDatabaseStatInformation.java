package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.theacropolis.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.theacropolis.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.PvEEventLibraryArchivesDatabaseStatInformation;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

public class PvEEventLibraryArchivesGrimoiresGraveyardDatabaseStatInformation extends PvEEventLibraryArchivesDatabaseStatInformation {

    @Field("fastest_game_finished")
    protected long fastestGameFinished = 0;

    @Override
    public void updateCustomStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGamePvEEventGrimoiresGraveyard;
        assert gamePlayer instanceof DatabaseGamePlayerPvEEventGrimoiresGraveyard;
        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        DatabaseGamePvEEventGrimoiresGraveyard eventGrimoiresGraveyard = (DatabaseGamePvEEventGrimoiresGraveyard) databaseGame;
        boolean won = eventGrimoiresGraveyard.getWavesCleared() == 1;
        int timeElapsed = eventGrimoiresGraveyard.getTimeElapsed();
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
