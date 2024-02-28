package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.grimoiresgraveyard;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePlayerPvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.grimoiresgraveyard.DatabaseGamePvEEventGrimoiresGraveyard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats implements MultiPvEEventLibraryArchivesGrimoiresGraveyardStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats());
        put(2, new DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats());
        put(3, new DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats());
        put(4, new DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats());
    }};

    public DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventGrimoiresGraveyard databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventGrimoiresGraveyard gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventLibraryArchivesGrimoiresGraveyardPlayerCountStats());
    }

    @Override
    public Collection<PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventLibraryArchivesGrimoiresGraveyardStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
