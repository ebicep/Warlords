package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.mithra.spidersdwelling;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePlayerPvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.mithra.spidersdwelling.DatabaseGamePvEEventSpidersDwelling;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats implements MultiPvEEventMithraSpidersDwellingStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats());
        put(2, new DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats());
        put(3, new DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats());
        put(4, new DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats());
    }};

    public DatabasePlayerPvEEventMithraSpidersDwellingDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventSpidersDwelling databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventSpidersDwelling gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventMithraSpidersDwellingPlayerCountStats());
    }

    @Override
    public Collection<PvEEventMithraSpidersDwellingStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventMithraSpidersDwellingStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
