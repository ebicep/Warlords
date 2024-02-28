package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.duel.classes.*;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerDuel implements DuelStatsWarlordsClasses {

    private DatabaseMageDuel mage = new DatabaseMageDuel();
    private DatabaseWarriorDuel warrior = new DatabaseWarriorDuel();
    private DatabasePaladinDuel paladin = new DatabasePaladinDuel();
    private DatabaseShamanDuel shaman = new DatabaseShamanDuel();
    private DatabaseRogueDuel rogue = new DatabaseRogueDuel();
    private DatabaseArcanistDuel arcanist = new DatabaseArcanistDuel();

    @Override
    public StatsWarlordsSpecs<DatabaseGameDuel, DatabaseGamePlayerDuel, DuelStats> getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
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
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
