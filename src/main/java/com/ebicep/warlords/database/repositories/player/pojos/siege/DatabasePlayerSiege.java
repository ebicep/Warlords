package com.ebicep.warlords.database.repositories.player.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsClasses;
import com.ebicep.warlords.database.repositories.player.pojos.StatsWarlordsSpecs;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.siege.classes.*;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatUtils;

public class DatabasePlayerSiege implements StatsWarlordsClasses<SiegeDatabaseStatInformation, StatsWarlordsSpecs<SiegeDatabaseStatInformation>> {

    private DatabaseMageSiege mage = new DatabaseMageSiege();
    private DatabaseWarriorSiege warrior = new DatabaseWarriorSiege();
    private DatabasePaladinSiege paladin = new DatabasePaladinSiege();
    private DatabaseShamanSiege shaman = new DatabaseShamanSiege();
    private DatabaseRogueSiege rogue = new DatabaseRogueSiege();
    private DatabaseArcanistSiege arcanist = new DatabaseArcanistSiege();

    @Override
    public void updateCustomStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        if (gamePlayer instanceof DatabaseGamePlayerSiege databaseGamePlayerSiege) {
            databaseGamePlayerSiege.getSpecStats().forEach((specializations, siegePlayer) -> {
                //UPDATE CLASS, SPEC
                this.getClass(Specializations.getClass(specializations))
                    .updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
                this.getSpec(specializations).updateCustomStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
            });
        } else {
            ChatUtils.MessageType.GAME.sendErrorMessage("DatabaseGamePlayerSiege is not an instance of DatabaseGamePlayerSiege");
        }
    }

}
