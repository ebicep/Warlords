package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats implements PvEEventMithraSpidersDwellingStatsWarlordsClasses {

    private DatabaseMagePvEEventMithraSpidersDwelling mage = new DatabaseMagePvEEventMithraSpidersDwelling();
    private DatabaseWarriorPvEEventMithraSpidersDwelling warrior = new DatabaseWarriorPvEEventMithraSpidersDwelling();
    private DatabasePaladinPvEEventMithraSpidersDwelling paladin = new DatabasePaladinPvEEventMithraSpidersDwelling();
    private DatabaseShamanPvEEventMithraSpidersDwelling shaman = new DatabaseShamanPvEEventMithraSpidersDwelling();
    private DatabaseRoguePvEEventMithraSpidersDwelling rogue = new DatabaseRoguePvEEventMithraSpidersDwelling();
    private DatabaseArcanistPvEEventMithraSpidersDwelling arcanist = new DatabaseArcanistPvEEventMithraSpidersDwelling();

    @Override
    public PvEEventMithraSpidersDwellingStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEEventSpidersDwelling databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventSpidersDwelling gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
