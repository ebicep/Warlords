package com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.forgottencodex;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePlayerPvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.libraryarchives.forgottencodex.DatabaseGamePvEEventForgottenCodex;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats implements MultiPvEEventLibraryArchivesForgottenCodexStats {

    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
        put(2, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
        put(3, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
        put(4, new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
    }};

    public DatabasePlayerPvEEventLibraryForgottenCodexDifficultyStats() {
    }

    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGamePvEEventForgottenCodex databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerPvEEventForgottenCodex gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEEventLibraryForgottenCodexPlayerCountStats());
    }


    @Override
    public Collection<PvEEventLibraryArchivesForgottenCodexStatsWarlordsClasses> getStats() {
        return playerCountStats.values()
                               .stream()
                               .map(PvEEventLibraryArchivesForgottenCodexStatsWarlordsClasses.class::cast)
                               .toList();
    }
}
