package com.ebicep.warlords.database.repositories.player.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.duel.classes.*;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerDuel implements StatsWarlordsClasses<DuelDatabaseStatInformation, StatsWarlordsSpecs<DuelDatabaseStatInformation>> {

    private DatabaseMageDuel mage = new DatabaseMageDuel();
    private DatabaseWarriorDuel warrior = new DatabaseWarriorDuel();
    private DatabasePaladinDuel paladin = new DatabasePaladinDuel();
    private DatabaseShamanDuel shaman = new DatabaseShamanDuel();
    private DatabaseRogueDuel rogue = new DatabaseRogueDuel();
    private DatabaseArcanistDuel arcanist = new DatabaseArcanistDuel();

    public void updateCustomStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        //UPDATE CLASS, SPEC
        this.getClass(Specializations.getClass(gamePlayer.getSpec())).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.getSpec(gamePlayer.getSpec()).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
    }

}
