package com.ebicep.warlords.database.repositories.player.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.cache.CachedMultiPvEWaveDefenseStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatTotals;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEWaveDefenseDifficultyStats implements CachedMultiPvEWaveDefenseStats {

    @Transient
    private final PushedStatTotals pushedStats = new PushedStatTotals();

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
        updateModeStats(databasePlayer, databaseGame, gamePlayer, result, multiplier, playersCollection);
    }

    /**
     * @return true if a player-count leaf was updated and local push-up was applied
     */
    public boolean updateModeStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEWaveDefense databaseGame,
            DatabaseGamePlayerPvEWaveDefense gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEWaveDefensePlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
            StatPushUp.applyPvE(pushedStats, gamePlayer, result, databaseGame, multiplier);
            return true;
        }
        return false;
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
    public Collection<WaveDefenseStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(WaveDefenseStatsWarlordsClasses.class::cast)
                               .toList();
    }

    @Override
    public PushedStatTotals pushedStats() {
        return pushedStats;
    }

    @Override
    public int treeWalkKills() {
        return CachedMultiPvEWaveDefenseStats.super.treeWalkKills();
    }

    @Override
    public Map<String, Long> treeWalkMobKills() {
        return CachedMultiPvEWaveDefenseStats.super.treeWalkMobKills();
    }

}
