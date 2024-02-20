package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEStatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats implements PvEStatsWarlordsClasses<DatabaseBasePvEEventBoltaroBonanza, PvEStatsWarlordsSpecs<DatabaseBasePvEEventBoltaroBonanza>> {

    private DatabaseMagePvEEventBoltaroBonanza mage = new DatabaseMagePvEEventBoltaroBonanza();
    private DatabaseWarriorPvEEventBoltaroBonanza warrior = new DatabaseWarriorPvEEventBoltaroBonanza();
    private DatabasePaladinPvEEventBoltaroBonanza paladin = new DatabasePaladinPvEEventBoltaroBonanza();
    private DatabaseShamanPvEEventBoltaroBonanza shaman = new DatabaseShamanPvEEventBoltaroBonanza();
    private DatabaseRoguePvEEventBoltaroBonanza rogue = new DatabaseRoguePvEEventBoltaroBonanza();
    private DatabaseArcanistPvEEventBoltaroBonanza arcanist = new DatabaseArcanistPvEEventBoltaroBonanza();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
        put(2, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
        put(3, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
        put(4, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
    }};

    public DatabasePlayerPvEEventBoltaroBonanzaDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEWaveDefense;

        super.updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE UNIVERSAL EXPERIENCE
        this.experience += gamePlayer.getExperienceEarnedUniversal() * multiplier;
        this.experiencePvE += gamePlayer.getExperienceEarnedUniversal() * multiplier;

        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }

    }

    public DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
    }

}
