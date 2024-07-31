package com.ebicep.warlords.database.repositories.player.pojos.ctf;

import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.TracksAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.classses.*;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Classes;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

public class DatabasePlayerCTF implements CTFStatsWarlordsClasses, TracksAbilityStats {

    private DatabaseMageCTF mage = new DatabaseMageCTF();
    private DatabaseWarriorCTF warrior = new DatabaseWarriorCTF();
    private DatabasePaladinCTF paladin = new DatabasePaladinCTF();
    private DatabaseShamanCTF shaman = new DatabaseShamanCTF();
    private DatabaseRogueCTF rogue = new DatabaseRogueCTF();
    private DatabaseArcanistCTF arcanist = new DatabaseArcanistCTF();
    @Field("ability_stats")
    private Map<String, AbstractAbilityStats<?>> abilityStats = new HashMap<>();

    @Override
    public Map<String, AbstractAbilityStats<?>> getAbilityStats() {
        return abilityStats;
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameCTF databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerCTF gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateSpecStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }

    @Override
    public CTFStatsWarlordsSpecs getClass(Classes classes) {
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
