package com.ebicep.warlords.database.repositories.player.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.tdm.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;

public class DatabasePlayerTDM implements StatsWarlordsClasses<TDMDatabaseStatInformation, StatsWarlordsSpecs<TDMDatabaseStatInformation>> {

    private DatabaseMageTDM mage = new DatabaseMageTDM();
    private DatabaseWarriorTDM warrior = new DatabaseWarriorTDM();
    private DatabasePaladinTDM paladin = new DatabasePaladinTDM();
    private DatabaseShamanTDM shaman = new DatabaseShamanTDM();
    private DatabaseRogueTDM rogue = new DatabaseRogueTDM();
    private DatabaseArcanistTDM arcanist = new DatabaseArcanistTDM();

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
