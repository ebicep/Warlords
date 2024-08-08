package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltaroslair;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePlayerPvEEventBoltarosLair;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltaroslair.DatabaseGamePvEEventBoltaroLair;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.TracksAbilityStats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventBoltaroLairStats implements MultiPvEEventBoltaroLairStats, TracksAbilityStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventBoltaroLairPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventBoltaroLairPlayerCountStats());
        put(2, new DatabasePlayerPvEEventBoltaroLairPlayerCountStats());
        put(3, new DatabasePlayerPvEEventBoltaroLairPlayerCountStats());
        put(4, new DatabasePlayerPvEEventBoltaroLairPlayerCountStats());
    }};
    @Field("ability_stats")
    private Map<Ability<?>, AbstractAbilityStats<?, ?>> abilityStats = new HashMap<>();

    @Override
    public Map<Ability<?>, AbstractAbilityStats<?, ?>> getAbilityStats() {
        return abilityStats;
    }

    public DatabasePlayerPvEEventBoltaroLairStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventBoltaroLair databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltarosLair gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventBoltaroLairPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
        updateAbilityStats(gamePlayer, multiplier);
    }

    public DatabasePlayerPvEEventBoltaroLairPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventBoltaroLairPlayerCountStats());
    }

    @Override
    public Collection<PvEEventBoltaroLairStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventBoltaroLairStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
