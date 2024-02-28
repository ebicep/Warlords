package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.narmer.narmerstomb;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePlayerPvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.narmer.narmerstomb.DatabaseGamePvEEventNarmersTomb;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats implements MultiPvEEventNarmerNarmersTombStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats());
        put(2, new DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats());
        put(3, new DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats());
        put(4, new DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats());
    }};

    public DatabasePlayerPvEEventNarmerNarmersTombDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventNarmersTomb databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventNarmersTomb gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventNarmerNarmersTombPlayerCountStats());
    }

    @Override
    public Collection<PvEEventNarmerNarmersTombStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventNarmerNarmersTombStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
