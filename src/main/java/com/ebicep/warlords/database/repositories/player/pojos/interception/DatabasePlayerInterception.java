package com.ebicep.warlords.database.repositories.player.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.interception.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerInterception implements StatsWarlordsClasses<InterceptionDatabaseStatInformation, StatsWarlordsSpecs<InterceptionDatabaseStatInformation>> {

    private DatabaseMageInterception mage = new DatabaseMageInterception();
    private DatabaseWarriorInterception warrior = new DatabaseWarriorInterception();
    private DatabasePaladinInterception paladin = new DatabasePaladinInterception();
    private DatabaseShamanInterception shaman = new DatabaseShamanInterception();
    private DatabaseRogueInterception rogue = new DatabaseRogueInterception();
    private DatabaseArcanistInterception arcanist = new DatabaseArcanistInterception();

    @Override
    public void updateCustomStats(
            DatabasePlayer databasePlayer, DatabaseGameBase databaseGame,
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
