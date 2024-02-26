package com.ebicep.warlords.database.repositories.player.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.siege.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerSiege implements SiegeStatsWarlordsClasses {

    private DatabaseMageSiege mage = new DatabaseMageSiege();
    private DatabaseWarriorSiege warrior = new DatabaseWarriorSiege();
    private DatabasePaladinSiege paladin = new DatabasePaladinSiege();
    private DatabaseShamanSiege shaman = new DatabaseShamanSiege();
    private DatabaseRogueSiege rogue = new DatabaseRogueSiege();
    private DatabaseArcanistSiege arcanist = new DatabaseArcanistSiege();

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameSiege databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerSiege gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        // SiegeStatsWarlordsClasses.super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection); OVERRIDE
        gamePlayer.getSpecStats()
                  .forEach((specializations, siegePlayer) -> getSpec(specializations).updateStats(databasePlayer,
                          databaseGame,
                          gameMode,
                          gamePlayer,
                          result,
                          multiplier,
                          playersCollection
                  ));
    }

    @Override
    public SiegeStatsWarlordsSpecs getClass(Classes classes) {
        return switch (classes) {
            case MAGE -> mage;
            case WARRIOR -> warrior;
            case PALADIN -> paladin;
            case SHAMAN -> shaman;
            case ROGUE -> rogue;
            case ARCANIST -> arcanist;
        };
    }
}
