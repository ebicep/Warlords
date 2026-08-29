package com.ebicep.warlords.database.repositories.player.pojos.pve.anomaly;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePlayerPvEAnomaly;
import com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly.DatabaseGamePvEAnomaly;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.TracksAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedMultiPvEStats;
import com.ebicep.warlords.database.repositories.player.pojos.cache.PushedStatTotals;
import com.ebicep.warlords.database.repositories.player.pojos.cache.StatPushUp;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerAnomalyStats implements PushedMultiPvEStats.Anomaly, TracksAbilityStats {

    @Transient
    private final PushedStatTotals pushedStats = new PushedStatTotals();

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEAnomalyPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEAnomalyPlayerCountStats());
        put(2, new DatabasePlayerPvEAnomalyPlayerCountStats());
        put(3, new DatabasePlayerPvEAnomalyPlayerCountStats());
        put(4, new DatabasePlayerPvEAnomalyPlayerCountStats());
    }};
    @Field("ability_stats")
    private Map<Ability<?>, AbstractAbilityStats<?, ?>> abilityStats = new HashMap<>();

    @Override
    public Map<Ability<?>, AbstractAbilityStats<?, ?>> getAbilityStats() {
        return abilityStats;
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEAnomaly databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEAnomaly gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        updateModeStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }

    /**
     * @return true if a player-count leaf was updated and local push-up was applied
     */
    public boolean updateModeStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEAnomaly databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEAnomaly gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEAnomalyPlayerCountStats countStats = getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
        updateAbilityStats(gamePlayer, multiplier);
        if (countStats != null) {
            StatPushUp.applyPvE(pushedStats, gamePlayer, result, databaseGame, multiplier);
            return true;
        }
        return false;
    }

    public DatabasePlayerPvEAnomalyPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEAnomalyPlayerCountStats());
    }

    @Override
    public Collection<AnomalyStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(AnomalyStatsWarlordsClasses.class::cast)
                               .toList();
    }

    @Override
    public PushedStatTotals pushedStats() {
        return pushedStats;
    }

}
