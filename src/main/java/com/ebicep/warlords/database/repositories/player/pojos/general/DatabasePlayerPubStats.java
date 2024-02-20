package com.ebicep.warlords.database.repositories.player.pojos.general;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGameCTF;
import com.ebicep.warlords.database.repositories.games.pojos.ctf.DatabaseGamePlayerCTF;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGameDuel;
import com.ebicep.warlords.database.repositories.games.pojos.duel.DatabaseGamePlayerDuel;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGamePlayerInterception;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGamePlayerTDM;
import com.ebicep.warlords.database.repositories.games.pojos.tdm.DatabaseGameTDM;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.MultiStat;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabasePlayerDuel;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabasePlayerInterception;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabasePlayerSiege;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabasePlayerTDM;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class DatabasePlayerPubStats implements MultiStat<DatabaseGameBase, DatabaseGamePlayerBase> {

    @Field("ctf_stats")
    private DatabasePlayerCTF ctfStats = new DatabasePlayerCTF();
    @Field("tdm_stats")
    private DatabasePlayerTDM tdmStats = new DatabasePlayerTDM();
    @Field("interception_stats")
    private DatabasePlayerInterception interceptionStats = new DatabasePlayerInterception();
    @Field("duel_stats")
    private DatabasePlayerDuel duelStats = new DatabasePlayerDuel();
    @Field("siege_stats")
    private DatabasePlayerSiege siegeStats = new DatabasePlayerSiege();

    public DatabasePlayerPubStats() {
    }


    @Override
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        switch (gameMode) {
            case CAPTURE_THE_FLAG -> {
                if (databaseGame instanceof DatabaseGameCTF ctfGame && gamePlayer instanceof DatabaseGamePlayerCTF ctfPlayer) {
                    this.ctfStats.updateStats(databasePlayer, ctfGame, ctfPlayer, multiplier, playersCollection);
                } else {
                    ChatUtils.MessageType.GAME.sendErrorMessage("CTF game or player is not an instance of the correct class!");
                }
            }
            case TEAM_DEATHMATCH -> {
                if (databaseGame instanceof DatabaseGameTDM tdmGame && gamePlayer instanceof DatabaseGamePlayerTDM tdmPlayer) {
                    this.tdmStats.updateStats(databasePlayer, tdmGame, tdmPlayer, multiplier, playersCollection);
                } else {
                    ChatUtils.MessageType.GAME.sendErrorMessage("TDM game or player is not an instance of the correct class!");
                }
            }
            case INTERCEPTION -> {
                if (databaseGame instanceof DatabaseGameInterception interceptionGame && gamePlayer instanceof DatabaseGamePlayerInterception interceptionPlayer) {
                    this.interceptionStats.updateStats(databasePlayer, interceptionGame, interceptionPlayer, multiplier, playersCollection);
                } else {
                    ChatUtils.MessageType.GAME.sendErrorMessage("Interception game or player is not an instance of the correct class!");
                }
            }
            case DUEL -> {
                if (databaseGame instanceof DatabaseGameDuel duelGame && gamePlayer instanceof DatabaseGamePlayerDuel duelPlayer) {
                    this.duelStats.updateStats(databasePlayer, duelGame, duelPlayer, multiplier, playersCollection);
                } else {
                    ChatUtils.MessageType.GAME.sendErrorMessage("Duel game or player is not an instance of the correct class!");
                }
            }
            case SIEGE -> {
                if (databaseGame instanceof DatabaseGameSiege siegeGame && gamePlayer instanceof DatabaseGamePlayerSiege siegePlayer) {
                    this.siegeStats.updateStats(databasePlayer, siegeGame, siegePlayer, multiplier, playersCollection);
                } else {
                    ChatUtils.MessageType.GAME.sendErrorMessage("Siege game or player is not an instance of the correct class!");
                }
            }
        }
    }

    public DatabasePlayerCTF getCtfStats() {
        return ctfStats;
    }

    public DatabasePlayerTDM getTdmStats() {
        return tdmStats;
    }

    public DatabasePlayerInterception getInterceptionStats() {
        return interceptionStats;
    }

    public DatabasePlayerDuel getDuelStats() {
        return duelStats;
    }

    @Override
    public <T extends StatsWarlordsClasses<?, ?, ?, ?>> List<T> getStats() {
        return List.of(
                (T) ctfStats,
                (T) tdmStats,
                (T) interceptionStats,
                (T) duelStats,
                (T) siegeStats
        );
    }


}
