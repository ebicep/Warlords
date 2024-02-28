package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.gardenofhesperides.theacropolis;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePlayerPvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.gardenofhesperides.theacropolis.DatabaseGamePvEEventTheAcropolis;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats implements MultiPvEEventGardenOfHesperidesTheAcropolisStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
        put(2, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
        put(3, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
        put(4, new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
    }};

    public DatabasePlayerPvEEventGardenOfHesperidesAcropolisDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventTheAcropolis databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventTheAcropolis gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventGardenOfHesperidesAcropolisPlayerCountStats());
    }

    @Override
    public Collection<PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventGardenOfHesperidesTheAcropolisStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
