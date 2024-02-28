package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.illumina.theborderlineofillusion;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePlayerPvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.illumina.theborderlineofillusion.DatabaseGamePvEEventTheBorderlineOfIllusion;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats implements MultiPvEEventIlluminaTheBorderLineOfIllusionStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
        put(2, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
        put(3, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
        put(4, new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
    }};

    public DatabasePlayerPvEEventTheBorderLineOfIllusionDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventTheBorderlineOfIllusion databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventTheBorderlineOfIllusion gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventTheBorderLineOfIllusionPlayerCountStats());
    }

    @Override
    public Collection<PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventIlluminaTheBorderLineOfIllusionStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
