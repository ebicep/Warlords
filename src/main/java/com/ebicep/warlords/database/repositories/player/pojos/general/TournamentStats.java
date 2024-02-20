package com.ebicep.warlords.database.repositories.player.pojos.general;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.duel.DatabasePlayerDuel;
import com.ebicep.warlords.database.repositories.player.pojos.interception.DatabasePlayerInterception;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.DatabasePlayerTDM;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import org.springframework.data.mongodb.core.mapping.Field;

public class TournamentStats {

    @Field("tournament_1_stats") // june 2022
    private DatabasePlayerTournamentStats tournament1Stats = new DatabasePlayerTournamentStats();
    @Field("tournament_2_stats") // june 2023
    private DatabasePlayerTournamentStats tournament2Stats = new DatabasePlayerTournamentStats();
    @Field("tournament_3_stats") // aug 2023
    private DatabasePlayerTournamentStats tournament3Stats = new DatabasePlayerTournamentStats();

    public TournamentStats() {
    }

    public DatabasePlayerTournamentStats getCurrentTournamentStats() {
        return this.tournament3Stats;
    }

    public static class DatabasePlayerTournamentStats extends DatabasePlayerGeneral {

        @Field("ctf_stats")
        private DatabasePlayerCTF ctfStats = new DatabasePlayerCTF();
        @Field("tdm_stats")
        private DatabasePlayerTDM tdmStats = new DatabasePlayerTDM();
        @Field("interception_stats")
        private DatabasePlayerInterception interceptionStats = new DatabasePlayerInterception();
        @Field("duel_stats")
        private DatabasePlayerDuel duelStats = new DatabasePlayerDuel();

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
            this.getClass(Specializations.getClass(gamePlayer.getSpec()))
                .updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            this.getSpec(gamePlayer.getSpec()).updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            switch (gameMode) {
                case CAPTURE_THE_FLAG -> this.ctfStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
                case TEAM_DEATHMATCH -> this.tdmStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
                case INTERCEPTION -> this.interceptionStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
                case DUEL -> this.duelStats.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            }
        }

        public DatabasePlayerCTF getCtfStats() {
            return ctfStats;
        }

        public void setCtfStats(DatabasePlayerCTF ctfStats) {
            this.ctfStats = ctfStats;
        }

        public DatabasePlayerTDM getTdmStats() {
            return tdmStats;
        }

        public void setTdmStats(DatabasePlayerTDM tdmStats) {
            this.tdmStats = tdmStats;
        }

        public DatabasePlayerInterception getInterceptionStats() {
            return interceptionStats;
        }

        public void setInterceptionStats(DatabasePlayerInterception interceptionStats) {
            this.interceptionStats = interceptionStats;
        }

        public DatabasePlayerDuel getDuelStats() {
            return duelStats;
        }

        public void setDuelStats(DatabasePlayerDuel duelStats) {
            this.duelStats = duelStats;
        }
    }
}
