package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePlayerPvEEventTartarus;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.tartarus.DatabaseGamePvEEventTartarus;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.tartarus.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventGardenOfHesperidesTartarusPlayerCountStats implements PvEEventGardenOfHesperidesTartarusStatsWarlordsClasses {

    private DatabaseMagePvEEventGardenOfHesperidesTartarus mage = new DatabaseMagePvEEventGardenOfHesperidesTartarus();
    private DatabaseWarriorPvEEventGardenOfHesperidesTartarus warrior = new DatabaseWarriorPvEEventGardenOfHesperidesTartarus();
    private DatabasePaladinPvEEventGardenOfHesperidesTartarus paladin = new DatabasePaladinPvEEventGardenOfHesperidesTartarus();
    private DatabaseShamanPvEEventGardenOfHesperidesTartarus shaman = new DatabaseShamanPvEEventGardenOfHesperidesTartarus();
    private DatabaseRoguePvEEventGardenOfHesperidesTartarus rogue = new DatabaseRoguePvEEventGardenOfHesperidesTartarus();
    private DatabaseArcanistPvEEventGardenOfHesperidesTartarus arcanist = new DatabaseArcanistPvEEventGardenOfHesperidesTartarus();

    @Override
    public PvEEventGardenOfHesperidesTartarusStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEEventTartarus databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventTartarus gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
