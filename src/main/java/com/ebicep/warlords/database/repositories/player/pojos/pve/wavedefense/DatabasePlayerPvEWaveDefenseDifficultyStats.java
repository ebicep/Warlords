package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStat;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabasePlayerPvEWaveDefenseDifficultyStats implements MultiStat<DatabaseGamePvEWaveDefense, DatabaseGamePlayerPvEWaveDefense> {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEWaveDefensePlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEWaveDefensePlayerCountStats());
        put(2, new DatabasePlayerPvEWaveDefensePlayerCountStats());
        put(3, new DatabasePlayerPvEWaveDefensePlayerCountStats());
        put(4, new DatabasePlayerPvEWaveDefensePlayerCountStats());
    }};

    public DatabasePlayerPvEWaveDefenseDifficultyStats() {
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
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEWaveDefensePlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEWaveDefensePlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEWaveDefensePlayerCountStats());
    }

    public Map<Integer, DatabasePlayerPvEWaveDefensePlayerCountStats> getPlayerCountStats() {
        return playerCountStats;
    }

    @Override
    public <T extends StatsWarlordsClasses<?, ?, ?, ?>> List<T> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(stats -> (T) stats)
                               .toList();
    }
}
