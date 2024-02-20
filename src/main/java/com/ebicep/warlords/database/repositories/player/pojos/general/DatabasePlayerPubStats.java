package com.ebicep.warlords.database.repositories.player.pojos.general;


import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabasePlayerDuel;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabasePlayerInterception;
import com.ebicep.warlords.database.repositories.player.pojos.siege.DatabasePlayerSiege;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabasePlayerTDM;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class DatabasePlayerPubStats implements StatsWarlordsSpecs<DatabaseGameBase, DatabaseGamePlayerBase, DatabaseSpecialization> {

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
    public List<List<DatabaseSpecialization>> getSpecs() {
        return List.of(
                List.of(ctfStats.getSpec()),
                List.of(tdmStats.getSpecs()),
                List.of(interceptionStats.getSpecs()),
                List.of(duelStats.getSpecs()),
                List.of(siegeStats.getSpecs())
        );
    }

    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        switch (gameMode) {
            case CAPTURE_THE_FLAG -> this.ctfStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            case TEAM_DEATHMATCH -> this.tdmStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            case INTERCEPTION -> this.interceptionStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            case DUEL -> this.duelStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            case SIEGE -> this.siegeStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
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
}
