package com.ebicep.warlords.database.repositories.player.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.ExperienceManager;

public interface Stats<DatabaseGameT extends DatabaseGameBase, DatabaseGamePlayerT extends DatabaseGamePlayerBase> {

    void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameT databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerT gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    );

    default void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameT databaseGame,
            DatabaseGamePlayerT gamePlayer,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateStats(databasePlayer, databaseGame, databaseGame.getGameMode(), gamePlayer, databaseGame.getPlayerGameResult(gamePlayer), multiplier, playersCollection);
    }

    default double getKDA() {
        if (getDeaths() <= 0) {
            return 0;
        }
        return (getKills() + getAssists()) / (double) getDeaths();
    }

    int getDeaths();

    int getKills();

    int getAssists();

    default double getKillsPerGame() {
        return getPlays() <= 0 ? 0 : (double) getKills() / getPlays();
    }

    int getPlays();

    default double getKillsAssistsPerGame() {
        return getPlays() <= 0 ? 0 : (double) (getKills() + getAssists()) / getPlays();
    }

    default double getDeathsPerGame() {
        return getPlays() <= 0 ? 0 : (double) getDeaths() / getPlays();
    }

    default double getWL() {
        if (getLosses() == 0) {
            return 0;
        }
        return (double) getWins() / getLosses();
    }

    int getLosses();

    int getWins();

    default double getWinRate() {
        return getPlays() <= 0 ? 0 : (double) getWins() / getPlays();
    }

    default long getDHPPerGame() {
        return getPlays() <= 0 ? 0 : getDHP() / getPlays();
    }

    default long getDHP() {
        return getDamage() + getHealing() + getAbsorbed();
    }

    long getDamage();

    long getHealing();

    long getAbsorbed();

    default int getLevel() {
        return ExperienceManager.getLevelFromExp(getExperience());
    }

    long getExperience();

}
