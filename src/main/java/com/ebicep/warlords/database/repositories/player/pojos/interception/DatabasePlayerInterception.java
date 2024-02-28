package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayerInterception;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.interception.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerInterception implements InterceptionStatsWarlordsClasses {

    private DatabaseMageInterception mage = new DatabaseMageInterception();
    private DatabaseWarriorInterception warrior = new DatabaseWarriorInterception();
    private DatabasePaladinInterception paladin = new DatabasePaladinInterception();
    private DatabaseShamanInterception shaman = new DatabaseShamanInterception();
    private DatabaseRogueInterception rogue = new DatabaseRogueInterception();
    private DatabaseArcanistInterception arcanist = new DatabaseArcanistInterception();

    @Override
    public StatsWarlordsSpecs<DatabaseGameInterception, DatabaseGamePlayerInterception, InterceptionStats> getClass(Classes classes) {
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
            DatabaseGameInterception databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerInterception gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
