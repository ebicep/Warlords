package com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePlayerPvEOnslaught;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEOnslaughtPlayerCountStats implements OnslaughtStatsWarlordsClasses {

    private DatabaseMagePvEOnslaught mage = new DatabaseMagePvEOnslaught();
    private DatabaseWarriorPvEOnslaught warrior = new DatabaseWarriorPvEOnslaught();
    private DatabasePaladinPvEOnslaught paladin = new DatabasePaladinPvEOnslaught();
    private DatabaseShamanPvEOnslaught shaman = new DatabaseShamanPvEOnslaught();
    private DatabaseRoguePvEOnslaught rogue = new DatabaseRoguePvEOnslaught();
    private DatabaseArcanistPvEOnslaught arcanist = new DatabaseArcanistPvEOnslaught();

    @Override
    public OnslaughtStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEOnslaught databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEOnslaught gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
