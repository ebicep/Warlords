package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePlayerPvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats implements PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsClasses {

    private DatabaseMagePvEEventGardenOfHesperidesAcropolis mage = new DatabaseMagePvEEventGardenOfHesperidesAcropolis();
    private DatabaseWarriorPvEEventGardenOfHesperidesAcropolis warrior = new DatabaseWarriorPvEEventGardenOfHesperidesAcropolis();
    private DatabasePaladinPvEEventGardenOfHesperidesAcropolis paladin = new DatabasePaladinPvEEventGardenOfHesperidesAcropolis();
    private DatabaseShamanPvEEventGardenOfHesperidesAcropolis shaman = new DatabaseShamanPvEEventGardenOfHesperidesAcropolis();
    private DatabaseRoguePvEEventGardenOfHesperidesAcropolis rogue = new DatabaseRoguePvEEventGardenOfHesperidesAcropolis();
    private DatabaseArcanistPvEEventGardenOfHesperidesAcropolis arcanist = new DatabaseArcanistPvEEventGardenOfHesperidesAcropolis();

    @Override
    public PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEEventTheAcropolis databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventTheAcropolis gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
