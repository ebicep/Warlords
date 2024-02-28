package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;

public class DatabasePlayerPvEWaveDefensePlayerCountStats implements WaveDefenseStatsWarlordsClasses {

    private DatabaseMagePvEWaveDefense mage = new DatabaseMagePvEWaveDefense();
    private DatabaseWarriorPvEWaveDefense warrior = new DatabaseWarriorPvEWaveDefense();
    private DatabasePaladinPvEWaveDefense paladin = new DatabasePaladinPvEWaveDefense();
    private DatabaseShamanPvEWaveDefense shaman = new DatabaseShamanPvEWaveDefense();
    private DatabaseRoguePvEWaveDefense rogue = new DatabaseRoguePvEWaveDefense();
    private DatabaseArcanistPvEWaveDefense arcanist = new DatabaseArcanistPvEWaveDefense();

    @Override
    public WaveDefenseStatsWarlordsSpecs getClass(Classes classes) {
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
            DatabaseGamePvEWaveDefense databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEWaveDefense gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }
}
