package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabasePlayerPvEDifficultyStats implements PvEStatsWarlordsClasses<DatabaseBasePvE, PvEStatsWarlordsSpecs<DatabaseBasePvE>> {

    private DatabaseMagePvE mage = new DatabaseMagePvE();
    private DatabaseWarriorPvE warrior = new DatabaseWarriorPvE();
    private DatabasePaladinPvE paladin = new DatabasePaladinPvE();
    private DatabaseShamanPvE shaman = new DatabaseShamanPvE();
    private DatabaseRoguePvE rogue = new DatabaseRoguePvE();
    private DatabaseArcanistPvE arcanist = new DatabaseArcanistPvE();
    @Field("player_count_stats")
    private Map<Integer, DatabasePlayerPvEPlayerCountStats> playerCountStats = new LinkedHashMap<>() {{
        put(1, new DatabasePlayerPvEPlayerCountStats());
        put(2, new DatabasePlayerPvEPlayerCountStats());
        put(3, new DatabasePlayerPvEPlayerCountStats());
        put(4, new DatabasePlayerPvEPlayerCountStats());
    }};

    public DatabasePlayerPvEDifficultyStats() {
    }

    public void updateCustomStats(
            com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert gamePlayer instanceof DatabaseGamePlayerPvEBase;

        //UPDATE CLASS, SPEC
        this.getSpec(gamePlayer.getSpec()).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);

        //UPDATE PLAYER COUNT STATS
        int playerCount = databaseGame.getBasePlayers().size();
        DatabasePlayerPvEPlayerCountStats countStats = this.getPlayerCountStats(playerCount);
        if (countStats != null) {
            countStats.updateStats(databasePlayer, databaseGame, gamePlayer, multiplier, playersCollection);
        } else {
            ChatUtils.MessageType.GAME_SERVICE.sendErrorMessage("Invalid player count = " + playerCount);
        }
    }

    public DatabasePlayerPvEPlayerCountStats getPlayerCountStats(int playerCount) {
        if (playerCount < 1) {
            return null;
        }
        return playerCountStats.computeIfAbsent(playerCount, k -> new DatabasePlayerPvEPlayerCountStats());
    }

    public Map<Integer, DatabasePlayerPvEPlayerCountStats> getPlayerCountStats() {
        return playerCountStats;
    }

    public void setPlayerCountStats(Map<Integer, DatabasePlayerPvEPlayerCountStats> playerCountStats) {
        this.playerCountStats = playerCountStats;
    }
}
