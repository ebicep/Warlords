package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePlayerPvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats implements PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsClasses {

    private DatabaseMagePvEEventTheBorderLineOfIllusion mage = new DatabaseMagePvEEventTheBorderLineOfIllusion();
    private DatabaseWarriorPvEEventTheBorderLineOfIllusion warrior = new DatabaseWarriorPvEEventTheBorderLineOfIllusion();
    private DatabasePaladinPvEEventTheBorderLineOfIllusion paladin = new DatabasePaladinPvEEventTheBorderLineOfIllusion();
    private DatabaseShamanPvEEventTheBorderLineOfIllusion shaman = new DatabaseShamanPvEEventTheBorderLineOfIllusion();
    private DatabaseRoguePvEEventTheBorderLineOfIllusion rogue = new DatabaseRoguePvEEventTheBorderLineOfIllusion();
    private DatabaseArcanistPvEEventTheBorderLineOfIllusion arcanist = new DatabaseArcanistPvEEventTheBorderLineOfIllusion();

    @Override
    public PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEEventTheBorderlineOfIllusion databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventTheBorderlineOfIllusion gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
