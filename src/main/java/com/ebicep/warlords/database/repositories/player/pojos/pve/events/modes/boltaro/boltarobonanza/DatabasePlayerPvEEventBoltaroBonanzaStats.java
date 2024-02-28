package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.boltarobonanza;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePlayerPvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza.DatabaseGamePvEEventBoltaroBonanza;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventBoltaroBonanzaStats implements MultiPvEEventBoltaroBonanzaStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
        put(2, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
        put(3, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
        put(4, new DatabasePlayerPvEEventBoltaroBonanzaPlayerCountStats());
    }};

    public DatabasePlayerPvEEventBoltaroBonanzaStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventBoltaroBonanza databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventBoltaroBonanza gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
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


    @Override
    public Collection<PvEEventBoltaroBonanzaStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventBoltaroBonanzaStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
